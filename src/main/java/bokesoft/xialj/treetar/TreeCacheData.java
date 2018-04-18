package bokesoft.xialj.treetar;

import java.util.Map;

public class TreeCacheData {
	/**
	 * 树形单元的集合,map缓存,key为当前单元的id
	 */
	private Map<Integer, TreeUnit> treeDataCache;
	/**
	 * 树形单元的层级集合,map缓存,key为当前单元的treelevel
	 */
	private Map<Integer, Map<Integer, TreeUnit>> treeLevelDataCache;	
	
	void setTreeDataCache(Map<Integer, TreeUnit> treeDataCache) {
		this.treeDataCache = treeDataCache;
	}	
	void setTreeLevelDataCache(Map<Integer, Map<Integer, TreeUnit>> treeLevelDataCache) {
		this.treeLevelDataCache = treeLevelDataCache;
	}
	public Map<Integer, TreeUnit> getTreeDataCache() {
		return treeDataCache;
	}
	public Map<Integer, Map<Integer, TreeUnit>> getTreeLevelDataCache() {
		return treeLevelDataCache;
	}
	
	public int TreeMaxLevel(){
		return treeLevelDataCache.keySet().size()-1;
	}
}
