package game.quadtree;

import game.Vec2;
import game.util.Rect;

public class QuadTree<Terminal> {
    int size;
    int sx, sy;
    QuadTreeNode<Terminal> root;

    public QuadTree(){
        root = null;
        size = 1;
        sx = 0;
        sy = 0;
    }

    public boolean isEmpty(){ return root == null; }

    public void walk(QuadTreeWalker<Terminal> walker){
        if(root != null) root.walk(walker);
    }

    public void walk(QuadTreePositionWalker<Terminal> walker){
        if(root != null) root.walk(walker, sx, sy, size);
    }

    public void walk(QuadTreeConditionalRectWalker<Terminal> walker){
        if(root != null) root.walk(walker, sx, sy, size);
    }

    public Terminal get(int x, int y){
        return root.get(x - sx, y - sy, size);
    }

    public QuadTreeIterator getIterator(){
        return new QuadTreeIterator(root, sx, sy, size);
    }

    public void remove(int x, int y){
        if(root != null) root = root.remove(x - sx, y - sy, size);
        if(root != null) root = root.reduce();
    }
    
    public void put(int x, int y, Terminal item){
        if(root == null){
            sx = x;
            sy = y;
            size = 2;
            root = new QuadTreeContainer(new TerminalNode(item), 0);
        }
        if(x >= sx){
            if(y >= sy){
                // QUAD 4 - maybe down/maybe right
                int greater = Math.max(1 + x - sx - size, 1 + y - sy - size);
                if(greater > 0){
                    growRightDown(greater);
                }
            }else{
                // QUAD 1 - grow up/maybe right
                int greater = Math.max(1 + x - sx - size, sy - y);
                growRightUp(greater);
            }
        }else{
            if(y >= sy){
                // QUAD 3 - maybe down/grow left
                int greater = Math.max(sx - x, 1 + y - sy - size);
                growLeftDown(greater);
            }else{
                // QUAD 2 - grow up/grow left
                int greater = Math.max(sx - x, sy - y);
                growLeftUp(greater);
            }
        }
        root.put(x - sx, y - sy, size, item);
    }

    private void growLeftDown(int extent) {
       while(extent > 0){
           extent -= size;
           sx -= size;
           root = new QuadTreeContainer(root, 3);
           size *= 2;
       }
    }

    private void growLeftUp(int extent) {
        while(extent > 0){
            extent -= size;
            sx -= size;
            sy -= size;
            root = new QuadTreeContainer(root, 2);
            size *= 2;
        }
    }

    private void growRightUp(int extent) {
        while(extent > 0){
            extent -= size;
            sy -= size;
            root = new QuadTreeContainer(root, 1);
            size *= 2;
        }
    }

    private void growRightDown(int extent) {
        while(extent > 0){
            extent -= size;
            root = new QuadTreeContainer(root, 0);
            size*=2;
        }
    }

    public class QuadTreeIterator {
        QuadTreeNode<Terminal> node;
        public int size;
        public int x, y;
        int corner = 0;
        public QuadTreeIterator(QuadTreeNode<Terminal> node, int x, int y, int size){
            this.node = node;
            this.x = x;
            this.y = y;
            this.size = size;
        }

        public boolean isTerminal(){
            return node instanceof TerminalNode;
        }

        public Rect getBounds(){
            return new Rect(x, y, size);
        }

        public void reset(){
            corner = 0;
        }

        public QuadTreeIterator getNext(){
            QuadTreeContainer container = (QuadTreeContainer)node;
            int subSize = size / 2;
            switch (corner){
                case 0:
                    corner++;
                    if(container.contents[0] == null){
                        return getNext();
                    }
                    return new QuadTreeIterator(container.contents[0], x, y, subSize);
                case 1:
                    corner++;
                    if(container.contents[1] == null){
                        return getNext();
                    }
                    return new QuadTreeIterator(container.contents[1], x, y + subSize, subSize);
                case 3:
                    corner++;
                    if(container.contents[3] == null){
                        return getNext();
                    }
                    return new QuadTreeIterator(container.contents[3], x + subSize, y, subSize);
                case 2:
                    corner++;
                    if(container.contents[2] == null){
                        return getNext();
                    }
                    return new QuadTreeIterator(container.contents[2], x + subSize, y + subSize, subSize);
                default:
                    return null;
            }
        }

        public Terminal getTerminal(){
            return ((TerminalNode)node).item;
        }
    }

    private interface QuadTreeNode<Terminal> {
        void put(int x, int y, int size, Terminal item);
        Terminal get(int x, int y, int size);
        QuadTreeNode<Terminal> remove(int x, int y, int size);
        QuadTreeNode<Terminal> reduce();

        void walk(QuadTreeWalker<Terminal> walker);
        void walk(QuadTreePositionWalker<Terminal> walker, int x, int y, int size);
        void walk(QuadTreeConditionalRectWalker<Terminal> walker, int x, int y, int size);
    }

    private class QuadTreeContainer implements QuadTreeNode<Terminal> {
        QuadTreeNode<Terminal>[] contents;

        public QuadTreeContainer(){
            contents = new QuadTreeNode[]{null, null, null, null};
        }

        public QuadTreeContainer(QuadTreeNode<Terminal> node, int pos){
            contents = new QuadTreeNode[]{null, null, null, null};
            contents[pos] = node;
        }

        public void put(int x, int y, int size, Terminal item){
            int subSize = size / 2;
            if(x < subSize){
                if(y < subSize){
                    putAt(0, x, y, subSize, item);
                }else{
                    putAt(1, x, y - subSize, subSize, item);
                }
            }else{
                if(y < subSize){
                    putAt(3,x - subSize, y, subSize, item);
                }else{
                    putAt(2,x - subSize, y - subSize, subSize, item);
                }
            }
        }

        @Override
        public Terminal get(int x, int y, int size) {
            int subSize = size / 2;
            if(x < subSize){
                if(y < subSize){
                    return contents[0] == null ? null : contents[0].get(x, y, subSize);
                }else{
                    return contents[1] == null ? null : contents[1].get(x, y - subSize, subSize);
                }
            }else{
                if(y < subSize){
                    return contents[3] == null ? null : contents[3].get(x - subSize, y, subSize);
                }else{
                    return contents[2] == null ? null : contents[2].get(x - subSize, y - subSize, subSize);
                }
            }
        }

        @Override
        public QuadTreeNode<Terminal> remove(int x, int y, int size) {
            int subSize = size / 2;
            if(x < subSize){
                if(y < subSize){
                    contents[0] = contents[0] == null ? null : contents[0].remove(x, y, subSize);
                }else{
                    contents[1] = contents[1] == null ? null : contents[1].remove(x, y - subSize, subSize);
                }
            }else {
                if (y < subSize) {
                    contents[3] = contents[3] == null ? null : contents[3].remove(x - subSize, y, subSize);
                } else {
                    contents[2] = contents[2] == null ? null : contents[2].remove(x - subSize, y - subSize, subSize);
                }
            }
            return isEmpty() ? null : this;
        }

        @Override
        public QuadTreeNode<Terminal> reduce() {
            if(isSingleton()){
                int singlet = getSinglet();
                if(contents[singlet] instanceof TerminalNode) return this;
                switch(singlet){
                    case 0: // TOP LEFT
                        size /= 2;
                        break;
                    case 1: // BOTTOM LEFT
                        size /= 2;
                        sy += size;
                        break;
                    case 2: // BOTTOM RIGHT
                        size /= 2;
                        sx += size;
                        sy += size;
                        break;
                    case 3: // TOP RIGHT
                        size /= 2;
                        sx += size;
                        break;
                }
                return contents[singlet].reduce();
            }
            return this;
        }

        private boolean isEmpty(){
            return  contents[0] == null &&
                    contents[1] == null &&
                    contents[3] == null &&
                    contents[2] == null;
        }

        private boolean isSingleton(){
            int c = 0;
            if(contents[0] != null) c++;
            if(contents[1] != null) c++;
            if(contents[3] != null) c++;
            if(contents[2] != null) c++;
            return c == 1;
        }

        private int getSinglet(){
            if(contents[0] != null) return 0;
            if(contents[1] != null) return 1;
            if(contents[3] != null) return 3;
            //if(contents[2] != null)
            return 2;
        }

        private void putAt(int index, int x, int y, int size, Terminal item){
            if(contents[index] == null){
                if(size == 1){
                    contents[index] = new TerminalNode();
                }else{
                    contents[index] = new QuadTreeContainer();
                }
                contents[index].put(x, y, size, item);
            }else{
                contents[index].put(x, y, size, item);
            }
        }

        @Override
        public void walk(QuadTreeWalker<Terminal> walker) {
            if (contents[0] != null) contents[0].walk(walker);
            if (contents[1] != null) contents[1].walk(walker);
            if (contents[3] != null) contents[3].walk(walker);
            if (contents[2] != null) contents[2].walk(walker);
        }

        @Override
        public void walk(QuadTreePositionWalker<Terminal> walker, int x, int y, int size) {
            int subSize = size / 2;
            if(contents[0] != null) contents[0].walk(walker, x, y, subSize);
            if(contents[1] != null) contents[1].walk(walker, x, y + subSize, subSize);
            if(contents[3] != null) contents[3].walk(walker, x + subSize, y, subSize);
            if(contents[2] != null) contents[2].walk(walker, x + subSize, y + subSize, subSize);
        }

        @Override
        public void walk(QuadTreeConditionalRectWalker<Terminal> walker, int x, int y, int size) {
            Vec2 s = new Vec2(size, size);
            if(walker.visit(new Rect(new Vec2(x, y), s), false, null)) {
                int subSize = size / 2;
                if (contents[0] != null) contents[0].walk(walker, x, y, subSize);
                if (contents[1] != null) contents[1].walk(walker, x, y + subSize, subSize);
                if (contents[3] != null) contents[3].walk(walker, x + subSize, y, subSize);
                if (contents[2] != null) contents[2].walk(walker, x + subSize, y + subSize, subSize);
            }
        }
    }

    private class TerminalNode implements QuadTreeNode<Terminal> {
        Terminal item;

        public TerminalNode(Terminal item) {
            this.item = item;
        }

        public TerminalNode() {
            item = null;
        }

        @Override
        public void put(int x, int y, int size, Terminal item) {
            this.item = item;
        }

        @Override
        public Terminal get(int x, int y, int size) {
            if(x == 0 && y == 0) return item;
            return null;
        }

        @Override
        public QuadTreeNode<Terminal> remove(int x, int y, int size) {
            if(x == 0 && y == 0) return null;
            return this;
        }

        @Override
        public QuadTreeNode<Terminal> reduce() {
            return this;
        }


        @Override
        public void walk(QuadTreeWalker<Terminal> walker) {
            walker.visit(item);
        }

        @Override
        public void walk(QuadTreePositionWalker<Terminal> walker, int x, int y, int size) {
            walker.visit(x, y, item);
        }

        @Override
        public void walk(QuadTreeConditionalRectWalker<Terminal> walker, int x, int y, int size) {
            walker.visit(new Rect(new Vec2(x, y), new Vec2(size, size)), true, item);
        }
    }
}
