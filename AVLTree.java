
/**
 *
 * AVLTree
 *
 * An implementation of a×ž AVL Tree with
 * distinct integer keys and info.
 *
 */

public class AVLTree {
	private static final IAVLNode EXTERNAL_LEAF = new AVLNode();

	// Legal Cases
	private static final int[] T_1 = {1,1};
	private static final int[] T_2 = {2,1};
	private static final int[] T_3 = {1,2};



	// Cases for Insert
	private static final int[] CASE_1L = {0,1};
	private static final int[] CASE_1R= {1,0};
	private static final int[] CASE_2L = {0,2};
	private static final int[] CASE_2L_B = {1,2};
	private static final int[] CASE_2R = {2,0};
	private static final int[] CASE_2R_B = {2,1};
	private static final int[] CASE_3L = {0,2};
	private static final int[] CASE_3L_B = {2,1};
	private static final int[] CASE_3R = {2,0};
	private static final int[] CASE_3R_B = {1,2};
	//special case for join
	private static final int[] CASE_4R = {2,0};
	private static final int[] CASE_4R_B = {1,1};
	private static final int[] CASE_4L = {0,2};
	private static final int[] CASE_4L_B = {1,1};
	// Cases for Delete
	private static final int[] DEL_CASE_1 = {2,2};
	private static final int[] DEL_CASE_2R = {1,3};
	private static final int[] DEL_CASE_2R_B = {1,1};
	private static final int[] DEL_CASE_2L = {3,1};
	private static final int[] DEL_CASE_2L_B = {1,1};
	private static final int[] DEL_CASE_3R = {1,3};
	private static final int[] DEL_CASE_3R_B = {1,2};
	private static final int[] DEL_CASE_3L = {3,1};
	private static final int[] DEL_CASE_3L_B = {2,1};
	private static final int[] DEL_CASE_4R = {1,3};
	private static final int[] DEL_CASE_4R_B = {2,1};
	private static final int[] DEL_CASE_4L = {3,1};
	private static final int[] DEL_CASE_4L_B = {1,2};



	//Tree fields
	private IAVLNode ROOT;
	private IAVLNode MIN;
	private IAVLNode MAX;

	public int MaxKey(){
		return this.MAX.getKey();
	}
	// empty builder
	public AVLTree() {
		this.ROOT = EXTERNAL_LEAF;
		this.MIN = EXTERNAL_LEAF;
		this.MAX = EXTERNAL_LEAF;
	}

	//builder (Tree node)
	public AVLTree(IAVLNode node) {
		this.ROOT = node;
		this.MIN = node;
		this.MAX = node;
		node.setParent(null);
	}

  /**
   * public boolean empty()
   *
   * Returns true if and only if the tree is empty.
   *
   *O(1)
   */
  public boolean empty() {
  	if (this.ROOT == null){return true;}
    return !(this.ROOT.isRealNode()); // Tree is empty iff ROOT isn't real
  }

 /**
   * public String search(int k)
   *
   * Returns the info of an item with key k if it exists in the tree.
   * otherwise, returns null.
  *
  * O(log(n))  (linear to height)
   */
  public String search(int k) {
	  if (this.empty()) { //empty tree
		  return null;
	  }
	  IAVLNode x = this.ROOT;
	  while (x != null && x.isRealNode()) {
		  if (x.getKey() == k) {
			  return x.getValue();
		  }
		  else if (k < x.getKey()) { //look in the left subtree
			  x = x.getLeft();
		  }
		  else {					//look in the right subtree
			  x = x.getRight();
		  }
	  }
	  return null;  // to be replaced by student code
  }

  /**
   * public int insert(int k, String i)
   *
   * Inserts an item with key k and info i to the AVL tree.
   * The tree must remain valid, i.e. keep its invariants.
   * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
   * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
   * Returns -1 if an item with key k already exists in the tree.
   *
   *
   *
   * O(T.ROOT.getHeight()) = O(log(n))
   */
   public int insert(int k, String i) {
	   IAVLNode z = new AVLNode(k, i);
	   int reBalances = 0;

	   if (this.empty()) { //empty tree, insert the new node as the root
		   this.ROOT = z;
		   this.MIN = z;
		   this.MAX = z;
		   return reBalances;
	   }
	   IAVLNode[] TrackArray = new IAVLNode[this.ROOT.getHeight()+1];
	   IAVLNode x = positionAndTrack(k,TrackArray);			//tracking our descent down the tree O(log(n))

	   if (k == x.getKey()) { //node with key k already exists
		   return -1;
	   }

	   else if (k < x.getKey()) { //insert as left son
	   		this.SetChildAndParent(z,x,false);
		   for (IAVLNode node : TrackArray){	// updating node sizes
			   if ((node != null)&&(node.isRealNode())){ node.setSize(node.getSize() + 1);}
		   }
	   }

	   else { //insert as right son
		   this.SetChildAndParent(z,x,true);
		   for (IAVLNode node : TrackArray) {  // updating node sizes
			   if ((node != null)&&(node.isRealNode())){ node.setSize(node.getSize() + 1);}
		   }
	   }

	   // BALANCING

	   //each iteration is bounded by a fixed amount of time,
	   // each iteration takes us one step higher towards the root thus the amount of iterations is O(log(n))

	   IAVLNode s = z.getParent();
	   reBalances = this.InsertionBalance(s);  // function does balancing and returns the number of balancing operations it took O(log(n))

	   // update min and max
	   if (z.getKey() > this.MAX.getKey()){this.MAX = z;}
	   if (z.getKey() < this.MIN.getKey()){this.MIN = z;}
	   return reBalances;
   }
	/**
	 * compare 2 arrays of height differences
	 *
	 * O(1)
	 */
   private static boolean EQUAL(int[] a,int[] b){
   	return ((a[0] == b[0]) && (a[1] == b[1]));
   }
	/**
	 * height++
	 *
	 * O(1)
	 */
	private void promote(IAVLNode node){
		node.setHeight(node.getHeight() + 1);
	}
	/**
	 * height--
	 *
	 * O(1)
	 */
	private void demote(IAVLNode node){
		node.setHeight(node.getHeight() - 1);
	}

	/**
	 * gets a node -> checks height differences with its children
	 *
	 * O(1)
	 */
	private int[] BalanceDiff(IAVLNode parent){
   		int[] balanceDiff = new int[2];
   		IAVLNode left = parent.getLeft();
   		IAVLNode right = parent.getRight();
   		balanceDiff[0] = parent.getHeight() - left.getHeight();
   		balanceDiff[1] = parent.getHeight() - right.getHeight();
   		return balanceDiff;
	}

	/**
	 * @ pre TrackArray.length = this.ROOT.getHeight()
	 * @ pre for all 0<i<this.ROOT.getHeight() : TrackArray[i] = null
	 * finds the position of node with key k, or where to place it if a node with key k does not exist
	 * also keeps track of nodes we passed through.
	 *
	 * O(log(n)) (linear to height)
	 */
	private IAVLNode positionAndTrack(int k, IAVLNode[] TrackArray) {
   		int i = 0;
		if (this.empty()) { //empty tree
			return null;
		}

		IAVLNode x = this.ROOT;
		IAVLNode y = new AVLNode();
		while (x.isRealNode()){
			y = x;
			TrackArray[i] = y;	// add each node to array
			i++;				// advance the array index
			if (x.getKey() == k) {
				return x;
			}
			else if (k < x.getKey()) { //look in the left subtree
				x = x.getLeft();
			}
			else { //look in the right subtree
				x = x.getRight();
			}
		}
		return y;
	}


  /**
   * public int delete(int k)
   *
   * Deletes an item with key k from the binary tree, if it is there.
   * The tree must remain valid, i.e. keep its invariants.
   * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
   * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
   * Returns -1 if an item with key k was not found in the tree.
   *
   *
   *
   * O(T.ROOT.getHeight()) = O(log(n))
   */
   public int delete(int k)
   {
   	if (this.empty()){return -1;} // if Tree is empty then k not in it
   		int reBalances = 0;
	   IAVLNode[] TrackArray = new IAVLNode[this.ROOT.getHeight()+1]; //empty array to track the path to find k
	   IAVLNode x = positionAndTrack(k,TrackArray);  // finding k in the tree while writing path to TrackArray O(log(n))

	   if (k == x.getKey()) { //node with key k already exists
		   	IAVLNode current = x.getParent(); // saving parent of x for balancing later
	   		if ((!x.getRight().isRealNode()) && (!x.getLeft().isRealNode())){ // if x is leaf
	   			if ((x.getParent() == null)||(!x.getParent().isRealNode())){ // if x is ROOT
	   				this.ROOT = EXTERNAL_LEAF;
	   				this.MAX = EXTERNAL_LEAF;
	   				this.MIN = EXTERNAL_LEAF;
				}
	   			if (x == x.getParent().getRight()){SetChildAndParent(EXTERNAL_LEAF,x.getParent(),true);} // attach EXTERNAL_LEAF as the right son
	   			else if (x == x.getParent().getLeft()){SetChildAndParent(EXTERNAL_LEAF,x.getParent(),false);} // attach EXTERNAL_LEAF as the right son
	   			for (IAVLNode node : TrackArray){
					if ((node != null) && (node.isRealNode())){ node.setSize(node.getSize() - 1);}  //subtracting 1 from the path to k
				}
			}
	   		else if((!x.getRight().isRealNode()) && (x.getLeft().isRealNode())){ // if x is unary node
	   			if ((x.getParent() == null)||(!x.getParent().isRealNode())){	// if x is ROOT
	   				this.ROOT = x.getLeft();
	   				this.ROOT.setParent(null);
				}
	   			else{
					if (x == x.getParent().getRight()){SetChildAndParent(x.getLeft(),x.getParent(),true);} // attach x.getLeft()  as the right son of x.getParent()
					else if (x == x.getParent().getLeft()){SetChildAndParent(x.getLeft(),x.getParent(),false);} // attach x.getLeft()  as the left son of x.getParent()
				}
				for (IAVLNode node : TrackArray){
					if ((node != null) && (node.isRealNode())){ node.setSize(node.getSize() - 1);}  //subtracting 1 from the path to k
				}
			}
	   		else if((x.getRight().isRealNode()) && (!x.getLeft().isRealNode())){	// if x is unary node
				if ((x.getParent() == null)||(!x.getParent().isRealNode())){		// if x is ROOT
					this.ROOT = x.getRight();
					this.ROOT.setParent(null);
				}
				else{
					if (x == x.getParent().getRight()){ SetChildAndParent(x.getRight(),x.getParent(),true);} // attach x.getRight()  as the left son of x.getParent()
					else if (x == x.getParent().getLeft()){ SetChildAndParent(x.getRight(),x.getParent(),false);} // attach x.getRight()  as the left son of x.getParent()
				}
				for (IAVLNode node : TrackArray){
					if ((node != null) && (node.isRealNode())){ node.setSize(node.getSize() - 1);}  //subtracting 1 from the path to k
				}
			}
	   		else{
	   			IAVLNode succ = this.successor(x);
	   			reBalances = delete(succ.getKey());							//deletes successor and returns the amount of the operations the Balancing took
	   			this.SetChildAndParent(x.getRight(), succ, true);
				this.SetChildAndParent(x.getLeft(), succ, false);
				succ.setSize(x.getSize());
				succ.setHeight(x.getHeight());
	   			if ((x.getParent() == null)||(!x.getParent().isRealNode())){
	   				this.ROOT = succ;
	   				succ.setParent(null);
				}else{
					if (x == x.getParent().getRight()){ SetChildAndParent(succ,x.getParent(),true);} // attach EXTERNAL_LEAF as the right son
					else if (x == x.getParent().getLeft()){ SetChildAndParent(succ,x.getParent(),false);} // attach EXTERNAL_LEAF as the right son
				}
			}

	   		//BALANCING

		   //each iteration is bounded by a fixed amount of time,
		   // each iteration takes us one step higher towards the root thus the amount of iterations is O(log(n))

		   reBalances += DeletionBalancing(current); // adding the amount of operations th balancing took

		   //FIND NEW MAX/MIN

		   	if (x == this.MIN) {
			   	this.MIN = NodeMIN(this.ROOT); // find new MIN
		   	}
		   	if (x == this.MAX) {
			   	this.MAX = NodeMAX(this.ROOT); // find new MAX
		   	}
		   	return reBalances;
	   }
	   return -1; // if k != x.getKey() then k not in Tree
   }

	/**
	 * function receives a starting node current for balancing and return the amount of operations the balancing took
	 *
	 * each iteration take a fixed amount of time, in each iteration we advance one node upwards
	 * thus, number of iterations is bounded by the height difference between ROOT and current:
	 *
	 * O(ROOT.getHeight()-current.getHeight()) = O(log(n)-current.getHeight())= O(log(n))
	 */
   private int DeletionBalancing(IAVLNode current){
   		int reBalances = 0;
	   if (current != null) {
		   int[] balanceDiff = this.BalanceDiff(current); // current is the parent node of the one we deleted
		   while (!((EQUAL(balanceDiff, T_1)) || (EQUAL(balanceDiff, T_2)) || (EQUAL(balanceDiff, T_3))) && (current != null) && (current.isRealNode())) { // if height differences aren't legal
			   IAVLNode Next = current.getParent(); // save the parent before rotations for later use
			   if (EQUAL(balanceDiff, DEL_CASE_1)) { // [2,2]
				   this.demote(current);
				   reBalances++;
			   }
			   else if ((EQUAL(balanceDiff, DEL_CASE_2L)) && (EQUAL(BalanceDiff(current.getRight()), DEL_CASE_2L_B))) { // current [3,1] right [1,1]
				   this.demote(current);
				   this.promote(current.getRight());
				   this.LeftRotate(current);
				   reBalances += 3;
			   }
			   else if ((EQUAL(balanceDiff, DEL_CASE_2R)) && (EQUAL(BalanceDiff(current.getLeft()), DEL_CASE_2R_B))) { // current [1,3] right [1,1]
				   this.demote(current);
				   this.promote(current.getLeft());
				   this.RightRotate(current);
				   reBalances += 3;
			   }
			   else if ((EQUAL(balanceDiff, DEL_CASE_3L)) && (EQUAL(BalanceDiff(current.getRight()), DEL_CASE_3L_B))) { // current [3,1] right [2,1]
				   this.demote(current);
				   this.demote(current);
				   this.LeftRotate(current);
				   reBalances += 2;
			   }
			   else if ((EQUAL(balanceDiff, DEL_CASE_3R)) && (EQUAL(BalanceDiff(current.getLeft()), DEL_CASE_3R_B))) { // current [1,3] right [1,2]
				   this.demote(current);
				   this.demote(current);
				   this.RightRotate(current);
				   reBalances += 2;
			   }
			   else if ((EQUAL(balanceDiff, DEL_CASE_4L)) && (EQUAL(BalanceDiff(current.getRight()), DEL_CASE_4L_B))) { // current [3,1] right [1,2]
				   this.demote(current);
				   this.demote(current);
				   this.demote(current.getRight());
				   this.promote(current.getRight().getLeft());
				   this.DoubleRightRotate(current);
				   reBalances += 5;
			   }
			   else if ((EQUAL(balanceDiff, DEL_CASE_4R)) && (EQUAL(BalanceDiff(current.getLeft()), DEL_CASE_4R_B))) { // current [1,3] right [2,1]
				   this.demote(current);
				   this.demote(current);
				   this.demote(current.getLeft());
				   this.promote(current.getLeft().getRight());
				   this.DoubleLeftRotate(current);
				   reBalances += 5;
			   }
			   current = Next;
			   if (current != null) {
				   balanceDiff = this.BalanceDiff(current);
			   }
		   }
	   }
	   return reBalances;
   }

   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty.
	*
	* O(1)
    */
   public String min() {
	   if (this.empty()){return null;}
	   return this.MIN.getValue();
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty.
	*
	* O(1)
    */
   public String max() {
   		if (this.empty()){return null;}

	   return this.MAX.getValue();
   }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   *
   * O(n)
   */
  public int[] keysToArray() {
	IAVLNode[] NodeArray = this.InOrderWalk(); // NodeArray is an array of nodes sorted by keys
	int[] KeyArray = new int[this.size()];		// create empty array in length of size to contain all keys
	for (int i = 0; i < this.size(); i++){
		KeyArray[i]=NodeArray[i].getKey();		// add the keys by order to KeyArray
	}
	return KeyArray;
  }

	/**
	 * private IAVLNode[] InOrderWalk
	 *
	 * Returns array of nodes sorted by keys
	 *
	 * O(n) by travelling in order through successor we will not go down through an edge we went up through
	 * thus we will pass each edge 2 times at most and number of edges in a tree is linear to number of nodes
	 */
  private IAVLNode[] InOrderWalk(){
  	IAVLNode current = NodeMIN(this.ROOT);
  	int pointer = 0;
  	IAVLNode[] result = new IAVLNode[this.size()];
  	while (pointer < this.size()){
  		result[pointer] = current;
		current = successor(current);
		pointer ++;
	}
  	return result;
  }

	/**
	 * private IAVLNode NodeMIN
	 *
	 * return the minimal node of the subtree whose root is node
	 *
	 * O(log(n)) (at most number of iterations is the height of the node which is less or equal to height of ROOT
	 * height of ROOT is O(log(n)) each iteration takes a fixed amount of time.
	 */
  private static IAVLNode NodeMIN(IAVLNode node){
  	if (!node.isRealNode()){return null;}	//if subtree is empty return null
  	while (node.getLeft().isRealNode()){	//advance to left child as long as it's real
  		node = node.getLeft();
  	}
  	return node;
  }
	/**
	 * private IAVLNode NodeMIN
	 *
	 * return the maximal node of the subtree whose root is node
	 *
	 * O(log(n)) (at most number of iterations is the height of the node which is less or equal to height of ROOT
	 * height of ROOT is O(log(n)) each iteration takes a fixed amount of time.
	 */

	private static IAVLNode NodeMAX(IAVLNode node){
		if (!node.isRealNode()){return null;}	//if subtree is empty return null
		while (node.getRight().isRealNode()){	//advance to left child as long as it's real
			node = node.getRight();
		}
		return node;
	}
	/**
	 * private IAVLNode successor(IAVLNode node)
	 *
	 * returns the node which is next in the order of keys
	 *
	 * O(log(n)) we either go up or down the tree, thus number of iteration is bounded by the height of ROOT
	 * which is O(log(n)), each iteration takes a fixed amount of time.
	 *
	 */
  private IAVLNode successor(IAVLNode node){
  	if (!node.isRealNode()){return null;}
  	if (node.getRight().isRealNode()){
  		return NodeMIN(node.getRight());
  	}
  	IAVLNode parent = node.getParent();
  	while ((parent != null)&&(parent.isRealNode()) && (node == parent.getRight())){
  		node = parent;
  		parent = node.getParent();
	}
  	return parent;
  }

  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   *
   * O(n)
   */
  public String[] infoToArray() {
	  IAVLNode[] NodeArray = this.InOrderWalk();	// NodeArray is an array of nodes sorted by keys
	  String[] ValueArray = new String[this.size()];	// create empty array in length of size to contain all values
	  for (int i = 0; i < this.size(); i++){
		  ValueArray[i]=NodeArray[i].getValue();	// add the values by order to ValueArray
	  }
	  return ValueArray;
  }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
	*
	* O(1)
    */
   public int size()
   {
   		if (this.empty()){return 0;}
	   return this.ROOT.getSize();
   }
   
   /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
	*
	* O(1)
    */
   public IAVLNode getRoot()
   {
	   return this.ROOT;
   }
   
   /**
    * public AVLTree[] split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
    * 
	* precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
    * postcondition: none
    */   
   public AVLTree[] split(int x) {
   		int H = this.ROOT.getHeight();
	   IAVLNode[] TrackArray = new IAVLNode[H+1];
	   IAVLNode splitNode = positionAndTrack(x,TrackArray);	
	   
	   IAVLNode leftSonNode = splitNode.getLeft();
	   AVLTree leftSonTree = new AVLTree();
	   if (leftSonNode.isRealNode()) {
		   leftSonTree = new AVLTree(leftSonNode);
		   splitNode.setLeft(EXTERNAL_LEAF);
		   leftSonNode.setParent(null);
	   }
	   
	   IAVLNode rightSonNode = splitNode.getRight();
	   AVLTree rightSonTree = new AVLTree();
	   if (rightSonNode.isRealNode()) {
		   rightSonTree = new AVLTree(rightSonNode);
		   splitNode.setRight(EXTERNAL_LEAF);
		   rightSonNode.setParent(null);
	   }

	   AVLTree t1 = leftSonTree;
	   AVLTree t2 = rightSonTree;
	   int i = H;
	   AVLTree SubTree;
	   while (i >= 0){
	   	IAVLNode current = TrackArray[i];
	   	if (current != null && current.isRealNode()){
	   		if (current.getKey() > x){ // adds to larger tree if key > x
	   			SubTree = new AVLTree(current.getRight());
	   			SubTree.getRoot().setParent(null);
	   			current.setLeft(EXTERNAL_LEAF);
	   			current.setRight(EXTERNAL_LEAF);
	   			current.getLeft().setParent(null);
				current.setParent(null);
				t2.join(current, SubTree);
			}else if(current.getKey() < x){
				SubTree = new AVLTree(current.getLeft());
				SubTree.getRoot().setParent(null);
				current.setLeft(EXTERNAL_LEAF);
				current.setRight(EXTERNAL_LEAF);
				current.getRight().setParent(null);
				current.setParent(null);
				t1.join(current, SubTree);
			}
		}
	   	i--;
	   }

	   // t1: min and max
	   if (t1.getRoot() != null && t1.ROOT.isRealNode())
	   t1.MIN = NodeMIN(t1.ROOT);
	   t1.MAX = NodeMAX(t1.ROOT);
	   
	   // t2: min and max
	   if (t2.getRoot() != null && t2.ROOT.isRealNode())
	   t2.MIN = NodeMIN(t2.ROOT);
	   t2.MAX = NodeMAX(t2.ROOT);
	   
			   
	   
	   AVLTree[] result = new AVLTree[2];
	   result[0] = t1;
	   result[1] = t2;
	   
	   return result;
	   
   }
   /**
    * public int join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	*
	* precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
    * postcondition: none
    */   
   public int join(IAVLNode x, AVLTree t) {
	   int result;
	   int H1 = -1; int H2 = -1;
	   if(!this.empty()){H1 = this.getRoot().getHeight();}		//get heights for height difference calculation
	   if(!t.empty()){H2 = t.getRoot().getHeight();}
	   if (H1 > H2){result = H1 - H2 + 1;}			//height difference calculation
	   else {result = H2 - H1 + 1;}					//height difference calculation
	   IAVLNode s;
	   if (x.getKey() > this.MAX.getKey() && x.getKey() < t.MIN.getKey()){
		   s = this.JoinHelper(this, x, t);	//s is the parent of x in new tree		O(|tree height - t height|)  O(log(n))
	   }else{
		   s = this.JoinHelper(t, x, this);		//s is the parent of x in new tree		O(|tree height - t height|)	O(log(n))
	   }

	   
	   //balancing  O(|tree height - t height|) each iteration is bounded by a fixed amount of time,
	   // each iteration takes us one step higher towards the root thus the amount of iterations is O(|tree height - t height|) <= O(log(n)-log(t.size()))
	   this.InsertionBalance(s);

	   return result;
   }

	/**
	 * function receives a starting node s for balancing and return the amount of operations the balancing took
	 *
	 * each iteration take a fixed amount of time, in each iteration we advance one node upwards
	 * thus, number of iterations is bounded by the height difference between ROOT and s:
	 *
	 * O(ROOT.getHeight()-s.getHeight()) = O(log(n)-s.getHeight())
	 */
   private int InsertionBalance(IAVLNode s){
   	   int rebalances = 0;
	   int[] balanceDiff = {0,0};
	   if (s != null){balanceDiff = this.BalanceDiff(s);}
	   while (!((EQUAL(balanceDiff, T_1)) || (EQUAL(balanceDiff, T_2)) || (EQUAL(balanceDiff, T_3))) && (s != null) && (s.isRealNode())) { // continue loop while the height differences are "legal"
		   IAVLNode parent = s.getParent();		//save the parent before the  rotations in order to advance higher at the iteration's end
		   if ((EQUAL(balanceDiff,CASE_1L))||((EQUAL(balanceDiff,CASE_1R)))) { // [0,1] or [1,0]
			   this.promote(s);
			   rebalances ++;
		   }else if ((EQUAL(balanceDiff, CASE_2L)) && (EQUAL(BalanceDiff(s.getLeft()), CASE_2L_B))) { // parent [0,2] left [1,2]
			   this.demote(s);
			   this.RightRotate(s);
			   rebalances += 2;
		   } else if ((EQUAL(balanceDiff, CASE_2R)) && (EQUAL(BalanceDiff(s.getRight()), CASE_2R_B))) { // parent [2,0] right [2,1]
			   this.demote(s);
			   this.LeftRotate(s);
			   rebalances += 2;
		   } else if ((EQUAL(balanceDiff, CASE_3L)) && (EQUAL(BalanceDiff(s.getLeft()), CASE_3L_B))) { // parent [0,2] left [2,1]
			   this.demote(s);
			   this.demote(s.getLeft());
			   this.promote(s.getLeft().getRight());
			   this.DoubleLeftRotate(s);
			   rebalances += 5;
		   } else if ((EQUAL(balanceDiff, CASE_3R)) && (EQUAL(BalanceDiff(s.getRight()), CASE_3R_B))) { // parent [2,0] right [1,2]
			   this.demote(s);
			   this.demote(s.getRight());
			   this.promote(s.getRight().getLeft());
			   this.DoubleRightRotate(s);
			   rebalances += 5;
		   }else if ((EQUAL(balanceDiff, CASE_4L)) && (EQUAL(BalanceDiff(s.getLeft()), CASE_4L_B))) { // parent [0,2] left [1,1]
			   this.promote(s.getLeft());
			   this.RightRotate(s);
			   rebalances += 2;
		   } else if ((EQUAL(balanceDiff, CASE_4R)) && (EQUAL(BalanceDiff(s.getRight()), CASE_4R_B))) { // parent [2,0] right [1,1]
			   this.promote(s.getRight());
			   this.LeftRotate(s);
			   rebalances += 2;
		   }
		   s = parent;		//advance upwards
		   if (s != null){balanceDiff = this.BalanceDiff(s);}
	   }
	   return rebalances;
   }


	/**
	 * T1.keys < x.key < T2.keys
	 *
	 * joins the two trees set the higher one's root as this.ROOT
	 *
	 * a loop of abs(T1.height - T2.height) iterations
	 *
	 * O(|T1.height - T2.height|)
	 */
	private IAVLNode JoinHelper(AVLTree T1, IAVLNode x, AVLTree T2){
		int H1 = -1;		//gets trees' information before joining
		int H2 = -1;
		if(!T1.empty()){H1 = T1.getRoot().getHeight();}		//get heights for height difference calculation
		if(!T2.empty()){H2 = T2.getRoot().getHeight();}
		int S1 = T1.size();
		int S2 = T2.size();
		IAVLNode tempMIN = T1.MIN;
		IAVLNode tempMAX = T2.MAX;
		IAVLNode parent = null;
		if ((T1.empty()) && (T2.empty())){ // if both trees are empty join just creates a tree with a single node
			this.ROOT = x;
			this.MIN = x;
			this.MAX = x;
			x.setParent(null);
			x.setLeft(EXTERNAL_LEAF);
			x.setRight(EXTERNAL_LEAF);
			return null;
		}
		else if (T1.empty()){
			this.ROOT = T2.getRoot();
			this.MIN = T2.MIN;
			this.MAX = T2.MAX;
			this.insert(x.getKey(),x.getValue());
			return null;
		} else if (T2.empty()){
			this.ROOT = T1.getRoot();
			this.MIN = T1.MIN;
			this.MAX = T1.MAX;
			this.insert(x.getKey(),x.getValue());
			return null;
		}else {
			IAVLNode[] Array;
			IAVLNode curr;
			if (H1 > H2) {        // if T1 is higher we'll join T2 to T1 and define new root as T1.ROOT
				Array = new IAVLNode[H1 - H2 + 1]; // array for tracing the descent
				int i = 0;
				curr = T1.getRoot();
				while (curr.getHeight() > H2) {                        // descending the rightmost branch of tree
					Array[i] = curr;
					i++;
					curr = curr.getRight();
				}
				x.setSize(T2.size() + curr.getSize() + 1);            // size of x is sum of its children + 1
				x.setHeight(H2 + 1);                                // sets height of x
				parent = curr.getParent();
				for (IAVLNode node : Array) {
					if ((node != null) && (node.isRealNode())) {
						node.setSize(node.getSize() + 1 + S2);
					} // each node we passed through gains T2.size + 1 new nodes in subtree
				}
				// Attaching nodes
				this.SetChildAndParent(x, parent, true);
				this.SetChildAndParent(T2.getRoot(), x, true);
				this.SetChildAndParent(curr, x, false);
				this.ROOT = T1.getRoot(); // setting new ROOT
			} else if (H1 < H2) {                 // if T1 is higher we'll join T1 to T2 and define new root as T2.ROOT
				int i = 0;
				curr = T2.getRoot();
				Array = new IAVLNode[H2 - H1 + 1]; // array for tracing the descent
				while (curr.getHeight() > H1) {            // descending the leftmost branch of tree
					Array[i] = curr;
					i++;
					curr = curr.getLeft();
				}
				x.setSize(T1.size() + curr.getSize() + 1);            // size of x is sum of its children + 1
				x.setHeight(H1 + 1);                                    // sets height of x
				parent = curr.getParent();
				for (IAVLNode node : Array) {
					if ((node != null) && (node.isRealNode())) {
						node.setSize(node.getSize() + 1 + S1);
					} // each node we passed through gains T1.size + 1 new nodes in subtree
				}
				// Attaching nodes
				this.SetChildAndParent(x, parent, false);
				this.SetChildAndParent(T1.getRoot(), x, false);
				this.SetChildAndParent(curr, x, true);
				this.ROOT = T2.getRoot();
			} else { // if Tree heights are equal
				x.setSize(S1+ S2 + 1);            // size of x is sum of its children + 1
				x.setHeight(H1 + 1);
				this.SetChildAndParent(T2.getRoot(), x, true);
				this.SetChildAndParent(T1.getRoot(), x, false);
				this.ROOT = x;
			}
		}
		if ((tempMIN != null) && (tempMIN.isRealNode())) {
			this.MIN = tempMIN;
		} // sets new MIN
		else {
			this.MIN = x;
		}
		if ((tempMAX != null) && (tempMAX.isRealNode())) {
			this.MAX = tempMAX;
		} // sets new MAX
		else {
			this.MAX = x;
		}
		return parent;

	}


	/**
	 * does a right rotation as taught in class
	 *
	 * takes a fixed amount of time : O(1)
	 */

   private void RightRotate(IAVLNode y){
	   IAVLNode grandParent = y.getParent();
	   IAVLNode x = y.getLeft();
	   IAVLNode b = x.getRight();
	   int newYSize = y.getSize()-x.getLeft().getSize()-1;	// computes the new size of y after rotation ("loses" x and its right subtree)
	   if (grandParent != null) {							//if grandparent != null than it is normal child and parent setting
		   if (y == grandParent.getLeft()) {				// x replaces y as child thus inheriting its place as child
			   SetChildAndParent(x,grandParent, false);
		   } else {											// x replaces y as child thus inheriting its place as child
			   SetChildAndParent(x,grandParent, true);
		   }
	   }else{ // if parent.getParent() == null than parent is the root
		   this.ROOT = x; // x switches place with y and so if y was the ROOT than x becomes thee new ROOT
		   x.setParent(null);
	   }
	   x.setSize(y.getSize());								// x becomes new root of subtree thus get the size of y
	   y.setSize(newYSize);
	   SetChildAndParent(y,x, true);					// setting children and parent according to the rotation algorithm
	   SetChildAndParent(b,y, false);

   }
   
	/**
	 * setting a child and it parent:
	 *
	 * POST:
	 * child.getParent == parent
	 * if inRight parent.getRight() == child
	 * else parent.getLeft() == child
	 *
	 * this is unless parent = null then only child.getParent()==parent
	 * if child == null or isn't real then only parent.get(Left/Right)() == child
	 *
	 * take a fixed amount of time : O(1)
	 */
   private void SetChildAndParent(IAVLNode child, IAVLNode parent, boolean inRight){
   	if (parent != null) {
		if (inRight) {            			//inRight == true then set child in right
			parent.setRight(child);
		} else {
			parent.setLeft(child);		//inRight == false then set child in left
		}
	}
   	if ((child != null) && (child.isRealNode())){
   		child.setParent(parent);
	   }
   }

	/**
	 * does a left rotation as taught in class
	 *
	 * takes a fixed amount of time : O(1)
	 */
	private void LeftRotate(IAVLNode y){
	   IAVLNode grandParent = y.getParent();
	   IAVLNode x = y.getRight();
	   IAVLNode b = x.getLeft();
	   int newYSize = y.getSize()-x.getRight().getSize()-1; // computes the new size of y after rotation ("loses" x and its right subtree)
	   if (grandParent != null) {							//if grandparent != null than it is normal child and parent setting
		   if (y == grandParent.getLeft()) {				// x replaces y as child thus inheriting its place as child
			   SetChildAndParent(x,grandParent, false);
		   } else {											// x replaces y as child thus inheriting its place as child
			   SetChildAndParent(x,grandParent, true);
		   }
	   }else{
		   this.ROOT = x;									//if grandparent == null than x is now root and it parent is grandparent == null
		   x.setParent(null);
	   }
	   x.setSize(y.getSize());								// x becomes new root of subtree thus get the size of y
	   y.setSize(newYSize);
	   SetChildAndParent(y,x, false);				// setting children and parent according to the rotation algorithm
	   SetChildAndParent(b,y, true);
   }

	/**
	 * does a left rotation as taught in class
	 *
	 * takes a fixed amount of time : O(1)
	 */

   private void DoubleRightRotate(IAVLNode parent){
   	this.RightRotate(parent.getRight());
   	this.LeftRotate(parent);
   }

	/**
	 * does a left rotation as taught in class
	 *
	 * takes a fixed amount of time : O(1)
	 */

   private void DoubleLeftRotate(IAVLNode parent){
   	this.LeftRotate(parent.getLeft());
   	this.RightRotate(parent);
   }
   


	/** 
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{	
		public int getKey(); // Returns node's key (for virtual node return -1).
		public String getValue(); // Returns node's value [info], for virtual node returns null.
		public void setLeft(IAVLNode node); // Sets left child.
		public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.
		public void setRight(IAVLNode node); // Sets right child.
		public IAVLNode getRight(); // Returns right child, if there is no right child return null.
		public void setParent(IAVLNode node); // Sets parent.
		public IAVLNode getParent(); // Returns the parent, if there is no parent return null.
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.
    	public void setHeight(int height); // Sets the height of the node.
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
		public int getSize();// Returns size of subtree whose root is this node, size of virtual node is 0
		public void setSize(int size);// Sets Size for the node

	}

   /** 
    * public class AVLNode
    *
    * If you wish to implement classes other than AVLTree
    * (for example AVLNode), do it in this file, not in another file. 
    * 
    * This class can and MUST be modified (It must implement IAVLNode).
    */
  public static class AVLNode implements IAVLNode{
  		// Node fields
		private int Key;
		private String Value;
		private IAVLNode Left;
		private IAVLNode Right;
		private IAVLNode Parent;
		private int Height;
		private int Size;

		// empty builder is for EXTERNAL_LEAF
		public AVLNode(){
			this.Parent = null;
			this.Left = null;
			this.Height = -1;
			this.Key = -1;
			this.Right = null;
			this.Value = null;
			this.Size = 0;
		}

		//builder (key,value)
	   public AVLNode(int key,String value){
		   this.Parent = null;
		   this.Left = EXTERNAL_LEAF;
		   this.Height = 0;
		   this.Key = key;
		   this.Right = EXTERNAL_LEAF;
		   this.Value = value;
		   this.Size = 1;
	   }
	   	public void setSize(int size){this.Size = size;}
	   	public int getSize(){return this.Size;}
		// getter and setters
		public int getKey()
		{
			return this.Key;
		}
		public String getValue()
		{
			return this.Value;
		}
		public void setLeft(IAVLNode node)
		{
			this.Left = node;
		}
		public IAVLNode getLeft()
		{
			return this.Left;
		}
		public void setRight(IAVLNode node)
		{
			this.Right = node;
		}
		public IAVLNode getRight()
		{
			return this.Right;
		}
		public void setParent(IAVLNode node)
		{
			this.Parent = node;
		}
		public IAVLNode getParent()
		{
			return this.Parent;
		}
		public boolean isRealNode()
		{
			if (this == null){return false;}
			return (this.getHeight()!=-1); // if Height = -1 then node is an EXTERNAL_LEAF
		}
	    public void setHeight(int height)
	    {
	      this.Height = height;
	    }
	    public int getHeight()
	    {
	      return this.Height;
	    }
	    

  }

}
  
