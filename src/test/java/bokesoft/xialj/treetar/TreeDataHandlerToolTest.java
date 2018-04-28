package bokesoft.xialj.treetar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class TreeDataHandlerToolTest{
	private static Map<Integer, TreeUnit> CAR_TYPE_TREE_MAP = new HashMap<Integer, TreeUnit>();
	private static Map<Integer, Map<Integer, TreeUnit>> CAR_TYPE_TREE_CACHE = new HashMap<Integer, Map<Integer, TreeUnit>>();
	
	public static void main(String[] args) {	
		TreeCacheData cacheData = TreeDataTarTool.initTreeData(testinit());
		CAR_TYPE_TREE_MAP = cacheData.getTreeDataCache();
		CAR_TYPE_TREE_CACHE = cacheData.getTreeLevelDataCache();
		TreeCompressData resultc1 = TreeDataTarTool.compress(cacheData,initCompTypeIDs1(),true,true);
		TreeCompressData resultc2 = TreeDataTarTool.compress(cacheData,initCompTypeIDs2(),true,true);
		TreeCompressData resultc3 = TreeDataTarTool.compress(cacheData,initCompTypeIDs2(),false,true);
		
		Set<Integer> exSourceIns = initExtendIncIDs();
		Set<Integer> exSourceEXs = initExtendExcIDs();
		Set<Integer> resulte1 = TreeDataTarTool.extension(cacheData,exSourceIns,null,-1,true);
		List<Integer> resulteL1= new ArrayList<Integer>(resulte1);
		Collections.sort(resulteL1);
		
		Set<Integer> resulte2 = TreeDataTarTool.extension(cacheData,exSourceIns,exSourceEXs,-1,true);
		List<Integer> resulteL2 = new ArrayList<Integer>(resulte2);
		Collections.sort(resulteL2);
		
		Set<Integer> resulte3 = TreeDataTarTool.extension(cacheData,exSourceIns,exSourceEXs,1,true);
		List<Integer> resulteL3 = new ArrayList<Integer>(resulte3);
		Collections.sort(resulteL3);
		
		showCompressResult(resultc1,initCompTypeIDs1());
		showCompressResult(resultc2,initCompTypeIDs2());
		showCompressResult(resultc3,initCompTypeIDs3());
		
		showExtendResult(resulteL1,exSourceIns,null);
		showExtendResult(resulteL2,exSourceIns,exSourceEXs);
		showExtendResult(resulteL2,exSourceIns,exSourceEXs);
		
		Set<Integer> resultfp = TreeDataTarTool.findDirectAncestors(cacheData, 32125, true);
		showFindParentsResult(resultfp,32125);
		
		try{
			TreeDataTarTool.compress(cacheData,initCompTypeIDs3(),true,false);
		}catch (Throwable e) {
			e.printStackTrace();
		}
		try{
			TreeDataTarTool.extension(cacheData,exSourceIns,null,-1,false);
		}catch (Throwable e) {
			e.printStackTrace();
		}
	
	}
	
	private static void showCompressResult(TreeCompressData result,Set<Integer> source){
		System.out.println("************************* COMPRESS RESULT ***************************");
		System.out.println("****** SOURCE:");
		System.out.println(source);
		System.out.println("****** RESULT-INCLUDES:");
		System.out.println(result.getIncludeIDs());
		System.out.println("****** RESULT-EXCLUDES:");
		System.out.println(result.getExcludeIDs());
		System.out.println("*********************************************************************");
	}
	
	private static void  showExtendResult(List<Integer> result,Set<Integer> sourceIn,Set<Integer> sourceEx){
		System.out.println("========================= EXTAND RESULT ===========================");
		System.out.println("====== SOURCE-INCLUDES:");
		System.out.println(sourceIn);
		System.out.println("====== SOURCE-EXCLUDES:");
		System.out.println(sourceEx);
		System.out.println("====== RESULT:");
		System.out.println(result);
		System.out.println("=====================================================================");
	}
	
	
	private static void showFindParentsResult(Set<Integer> result,int sourceId){
		System.out.println("************************* FIND PARENT RESULT ***************************");
		System.out.println("****** SOURCE:");
		System.out.println(sourceId);
		System.out.println("****** RESULT:");
		System.out.println(result);
		System.out.println("*********************************************************************");
	}

	private static Set<TreeUnit> testinit() {
		Set<TreeUnit> result = new HashSet<TreeUnit>();
		CAR_TYPE_TREE_MAP.clear();
		CAR_TYPE_TREE_CACHE.clear();
		for(int i = 1; i < 10; i++){
			TreeUnit tree = new TreeUnit(i, -1);
			result.add(tree);
			for (int j = 0; j < 25; j++) {
				int ctId = i*100+j;
				TreeUnit childtree = new TreeUnit(ctId, i);
				result.add(childtree);
				for (int k = 0; k < 50; k++) {
					int gctId = ctId*100+k;
					TreeUnit gchildtree = new TreeUnit(gctId, ctId);
					result.add(gchildtree);
				}
			}
		}
		return result;
	}
	
	private static Set<Integer> initCompTypeIDs1() {
		Set<Integer> dealTypeIDs = new HashSet<Integer>();
		for(int i=10000 ; i < 13000; i++){
			if(i%100 < 50 && (i/100)%100 < 25){
				dealTypeIDs.add(i);
			}
		}
		for(int i=20000 ; i < 21000; i++){
			if(i%100 < 50 && (i/100)%100 < 25){
				dealTypeIDs.add(i);
			}
		}
		return dealTypeIDs;
	}
	
	private static Set<Integer> initCompTypeIDs2() {
		Set<Integer> dealTypeIDs = new HashSet<Integer>();
		for(int i=10000 ; i < 13000; i++){
			if(i%100 < 50 && (i/100)%100 < 25){
				dealTypeIDs.add(i);
			}
		}
		for(int i=20000 ; i < 22000; i++){
			if(i%100 < 50 && (i/100)%100 < 25){
				dealTypeIDs.add(i);
			}
		}
		return dealTypeIDs;
	}
	
	private static Set<Integer> initCompTypeIDs3() {
		Set<Integer> dealTypeIDs = new HashSet<Integer>();
		dealTypeIDs.add(99999);
		for(int i=10000 ; i < 13000; i++){
			if(i%100 < 50 && (i/100)%100 < 25){
				dealTypeIDs.add(i);
			}
		}
		for(int i=20000 ; i < 22000; i++){
			if(i%100 < 50 && (i/100)%100 < 25){
				dealTypeIDs.add(i);
			}
		}
		return dealTypeIDs;
	}
	
	private static Set<Integer> initExtendIncIDs() {
		Set<Integer> dealTypeIDs = new HashSet<Integer>();
		dealTypeIDs.add(1);
		dealTypeIDs.add(311);
		dealTypeIDs.add(32125);
		dealTypeIDs.add(402);
		return dealTypeIDs;
	}
	
	private static Set<Integer> initExtendExcIDs() {
		Set<Integer> dealTypeIDs = new HashSet<Integer>();
		dealTypeIDs.add(1);
		dealTypeIDs.add(311);
		dealTypeIDs.add(32125);
		dealTypeIDs.add(402);
		dealTypeIDs.add(432);
		return dealTypeIDs;
	}
}
