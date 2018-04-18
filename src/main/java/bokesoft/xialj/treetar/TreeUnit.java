package bokesoft.xialj.treetar;

import java.util.List;

public class TreeUnit {
	
	/**
	 * 树形单元id
	 */
	private int id;
	/**
	 * 树形单元父节点id
	 */
	private int parentId;
	/**
	 * 树形单元当前层级
	 */
	private int treeLevel;
	
	private List<TreeUnit> children;	
	
	public int getId() {
		return id;
	}
	
	void setParentId(int parentId){
		this.parentId = parentId;
	}
	
	public int getParentId() {
		return parentId;
	}
	void setTreeLevel(int treeLevel) {
		this.treeLevel = treeLevel;
	}
	public int getTreeLevel() {
		return treeLevel;
	}
	public List<TreeUnit> getChildren() {
		return children;
	}
	public void setChildren(List<TreeUnit> children) {
		this.children = children;
	}
	
	public TreeUnit(int id,int parentId){
		this.id = id;
		this.parentId = parentId;
	}
}
