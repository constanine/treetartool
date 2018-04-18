package bokesoft.xialj.treetar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TreeDataTarTool {
	private static final int NODE_ALL_ID = 0;
	/**
	 * 初始化树形结构缓存
	 * @param treeSet 树形单元TreeUnit的集合
	 * @return
	 */
	public static TreeCacheData initTreeData(Set<TreeUnit> treeSet) {		
		TreeCacheData result = new TreeCacheData();
		Map<Integer, TreeUnit> treeMap = new HashMap<Integer, TreeUnit>();
		for(TreeUnit treeUnit:treeSet){
			treeMap.put(treeUnit.getId(), treeUnit);
		}
		result.setTreeDataCache(treeMap);
		Map<Integer, Map<Integer, TreeUnit>> treeDataCache = new HashMap<Integer, Map<Integer, TreeUnit>>();
		result.setTreeLevelDataCache(treeDataCache);
		
		//准备全选节点,虚拟节点
		TreeUnit allTree = new TreeUnit(NODE_ALL_ID,-1);
		allTree.setTreeLevel(-1);
		List<TreeUnit> atchildren = new ArrayList<TreeUnit>();
		allTree.setChildren(atchildren);
		Map<Integer, TreeUnit> allTreeMap = new HashMap<Integer, TreeUnit>();
		allTreeMap.put(NODE_ALL_ID, allTree);
		treeDataCache.put(-1, allTreeMap);
		
		//所有处理过的数据,是要被移除的减少循环次数	
		Set<TreeUnit> dealedKeySet = new HashSet<TreeUnit>();
		
		//循环构建树形结构
		int treeLevel = 0;
		int unDealedSize = -1;
		int dealedSize = -1;
		do{
			unDealedSize = treeSet.size();
			Map<Integer, TreeUnit> curLevelTreeMap = new HashMap<Integer, TreeUnit>();
			for (TreeUnit treeUnit : treeSet) {
				if(treeUnit.getParentId() <= 0){
					treeUnit.setParentId(0);
					treeUnit.setTreeLevel(treeLevel);
					curLevelTreeMap.put(treeUnit.getId(), treeUnit);
					atchildren.add(treeUnit);
					dealedKeySet.add(treeUnit);
				}else if(treeDataCache.get(treeLevel-1).containsKey(treeUnit.getParentId())){
					TreeUnit parent = treeMap.get(treeUnit.getParentId());
					List<TreeUnit> pchildren = parent.getChildren();
					if(null == pchildren){
						pchildren = new ArrayList<TreeUnit>();
					}
					pchildren.add(treeUnit);
					parent.setChildren(pchildren);
					treeUnit.setTreeLevel(treeLevel);
					curLevelTreeMap.put(treeUnit.getId(), treeUnit);
					dealedKeySet.add(treeUnit);
				}
			}
			if(!curLevelTreeMap.isEmpty()){
				treeDataCache.put(treeLevel, curLevelTreeMap);
			}
			treeSet.removeAll(dealedKeySet);
			dealedKeySet.clear();
			dealedSize = treeSet.size();
			treeLevel++;
		}while(unDealedSize != dealedSize);
		
		//如果经过以上处理还有找不到父节点集合时,剩余的节点则当做根节点
		Map<Integer, TreeUnit> LeveL0TreeMap = treeDataCache.get(0);
		for (TreeUnit treeUnit : treeSet) {
			treeUnit.setParentId(0);
			treeUnit.setTreeLevel(0);
			LeveL0TreeMap.put(treeUnit.getId(), treeUnit);
			atchildren.add(treeUnit);
		}		
		return result;
	}
	
	/**
	 * 压缩树形结构下的id集合,尽量使用父节点id代替,其子节点集合
	 * 如果是用了uesExclude,则不一定非要全部子节点齐全才使用父节点代替,在其子节点满足了70%的比率就是用父节点id代替,但是会在exclude集合中出现
	 * 其他没有出现的兄弟节点id	 * 
	 * @param cacheData 通过初始化后的树形数据缓存
	 * @param sourceTypeIDs 目标id集合
	 * @param uesExclude 是否使用exclude算法
	 * @param ignoreMissedIDs 是否忽略不存在的id,如果不忽略则发现就报错,如果忽略则不处理
	 * @return
	 */
	public static TreeCompressData compress(TreeCacheData cacheData, Set<Integer> sourceTypeIDs,
			boolean uesExclude,boolean ignoreMissedIDs) {
		Map<Integer, Map<Integer, TreeUnit>> treeLevelDataCache = cacheData.getTreeLevelDataCache();
		Map<Integer, TreeUnit> treeDataCache = cacheData.getTreeDataCache();
		
		TreeCompressData dealedData = new TreeCompressData();
		Set<Integer> includeIDs = new HashSet<Integer>();
	    Set<Integer> excludeIDs = new HashSet<Integer>();
		List<Integer> typeIdList = new ArrayList<Integer>();
		typeIdList.addAll(sourceTypeIDs);
		int curTlevel = treeLevelDataCache.size();
		do {
			for (int idx = 0; idx < typeIdList.size();idx++) {
				int typeID = typeIdList.get(idx);
				TreeUnit tbean = treeDataCache.get(typeID);
				
				if(null == tbean){
					if(!ignoreMissedIDs){
						throw new RuntimeException(">>> [ID]"+typeID+",在树结构中不不存在,请检查");
					}
				}else{
					int parentID = tbean.getParentId();
					int parentTLevel = tbean.getTreeLevel() - 1;
					TreeUnit pbean = treeLevelDataCache.get(parentTLevel).get(parentID);
					List<TreeUnit> pchildren = pbean.getChildren();
					List<Integer> brothers = new ArrayList<Integer>();
					for (TreeUnit pchild : pchildren) {
						brothers.add(pchild.getId());
					}
					
					if (_checkRateWithCompression(typeIdList,excludeIDs,brothers,uesExclude)) {
						typeIdList.removeAll(brothers);
						typeIdList.add(parentID);
						idx--; //当前节点,一定是兄弟中第一个出现的节点,全部移除所有兄弟节点,不会影响当前下标之前的节点,不需要++;
					}				
				}
			}
			curTlevel--;
		} while (curTlevel > -1);
		includeIDs.addAll(typeIdList);
		dealedData.setExcludeIDs(excludeIDs);
		dealedData.setIncludeIDs(includeIDs);
		return dealedData;
	}
	
	/**
	 * 根据当前节点,向上其所有祖先,结果不包含自己
	 * @param cacheData 通过初始化后的树形数据缓存
	 * @param id
	 * @param ignoreMissedIDs 是否忽略不存在的id,如果不忽略则发现就报错,如果忽略则不处理
	 * @return
	 */
	public static Set<Integer> findParents(TreeCacheData cacheData,int id,boolean ignoreMissedIDs){
		Set<Integer> result = new HashSet<Integer>();
		Map<Integer, TreeUnit> treeDataCache = cacheData.getTreeDataCache();
		TreeUnit tbean = treeDataCache.get(id);
		if(null == tbean){
			if(ignoreMissedIDs){
				throw new RuntimeException(">>> [ID]"+id+",在树结构中不不存在,请检查");
			}
		}else{
			int parentID = tbean.getParentId();
			result.add(parentID);
			while(parentID > 0){
				TreeUnit pbean = treeDataCache.get(parentID);
				parentID = pbean.getParentId();
				result.add(parentID);
			}
		}		
		return result;
	}
		
	/**
	 * 根据其压缩结构的结果集反向逆推出所有的子集id集合,如果指定的targetLevel不是-1,则推到指定等级,就不在往下推行,
	 * @param cacheData 通过初始化后的树形数据缓存
	 * @param compressData 压缩后的结果集 TreeCompressData实例
	 * @param targetLevel 指定截止层级,-1则一步到底
	 * @param ignoreMissedIds 是否忽略不存在的id,如果不忽略则发现就报错,如果忽略则不处理
	 * @return
	 */
	public static Set<Integer> extension(TreeCacheData cacheData, TreeCompressData compressData,
			int targetLevel,boolean ignoreMissedIds) {
		return extension(cacheData, compressData.getIncludeIDs(),compressData.getExcludeIDs(),
				targetLevel, ignoreMissedIds);
	}
	
	/**
	 * 根据其压缩结构的结果集反向逆推出所有的子集id集合,如果指定的targetLevel不是-1,则推到指定等级,就不在往下推行,
	 * @param cacheData 通过初始化后的树形数据缓存
	 * @param includeIDs 正向结果集
	 * @param excludeIDs 反向结果集
	 * @param targetLevel 指定截止层级,-1则一步到底
	 * @param ignoreMissedIds 是否忽略不存在的id,如果不忽略则发现就报错,如果忽略则不处理
	 * @return
	 */
	public static Set<Integer> extension(TreeCacheData cacheData, Set<Integer> includeIDs,
			Set<Integer> excludeIDs,int targetLevel, boolean ignoreMissedIds) {
		Map<Integer, TreeUnit> treeMap = cacheData.getTreeDataCache();
		Set<Integer> result = new HashSet<Integer>();
		for (Integer sourceTypeID : includeIDs) {
			TreeUnit tbean = treeMap.get(sourceTypeID);
			if(null != tbean){
				if(targetLevel >0 &&  tbean.getTreeLevel() >= targetLevel ){
					if(null != excludeIDs && excludeIDs.contains(sourceTypeID)){
						//DO NOTHING
					}else{
						result.add(sourceTypeID);
					}
				}else{
					List<TreeUnit> children = tbean.getChildren();
					if (null != children) {
						_extensionChildren(result, children,excludeIDs,targetLevel);
					} else {
						if(null != excludeIDs && excludeIDs.contains(sourceTypeID)){
							//DO NOTHING
						}else{
							result.add(sourceTypeID);
						}
					}
				}
			}else{
				if(!ignoreMissedIds){
					throw new RuntimeException(">>> [ID]"+sourceTypeID+",在树结构中不不存在,请检查");
				}
			}
		}
		return result;
	}
	
	/**检查是否满足了当前集合中满足了所有兄弟,如果有则返回true,如果是用了uesExclude,则比如大于0.7则同样返回true
	 * 
	 * @param typeIdList 压缩源id集合
	 * @param excludeIDs 反向集合
	 * @param brothers 兄弟集合
	 * @param uesExclude 
	 * @return
	 */
	private static boolean _checkRateWithCompression(List<Integer> typeIdList, Set<Integer> excludeIDs,
			List<Integer> brothers,boolean uesExclude) {
		Set<Integer> unmatchedSet = new HashSet<Integer>();
		for(Integer brother:brothers){
			if(!typeIdList.contains(brother)){
				unmatchedSet.add(brother);	
			}
		}
		BigDecimal usSize = new BigDecimal(unmatchedSet.size());
		BigDecimal bSize = new BigDecimal(brothers.size());
		if(uesExclude){
			if(usSize.divide(bSize, 3, BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal("0.3"))<0){
				excludeIDs.addAll(unmatchedSet);
				return true;
			}
		}else{
			if(unmatchedSet.isEmpty()){
				excludeIDs.addAll(unmatchedSet);
				return true;
			}
		}		
		return false;
	}
	
	private static void _extensionChildren(Set<Integer> result, List<TreeUnit> children,Set<Integer> excludeIDs,
			int targetLevel) {
		for (TreeUnit child : children) {
			if(targetLevel >0 && child.getTreeLevel() >= targetLevel){
				if(null != excludeIDs && excludeIDs.contains(child.getId())){
					//DO NOTHING
				}else{
					result.add(child.getId());
				}
			}else{
				List<TreeUnit> gchildren = child.getChildren();
				if (null != gchildren) {
					_extensionChildren(result, gchildren,excludeIDs,targetLevel);
				} else {
					if(null != excludeIDs && excludeIDs.contains(child.getId())){
						//DO NOTHING
					}else{
						result.add(child.getId());
					}		
				}
				
			}
		}
	}
}
