package bokesoft.xialj.treetar;

import java.util.Set;

public class TreeCompressData {
	/**
	 * 压缩后,正向压缩集合
	 */
	private Set<Integer> includeIDs;
	/**
	 * 压缩后,反向压缩集合,在扩展时,会被剃掉
	 */
	private Set<Integer> excludeIDs;
	
	void setIncludeIDs(Set<Integer> includeIDs) {
		this.includeIDs = includeIDs;
	}
	void setExcludeIDs(Set<Integer> excludeIDs) {
		this.excludeIDs = excludeIDs;
	}
	public Set<Integer> getIncludeIDs() {
		return includeIDs;
	}
	public Set<Integer> getExcludeIDs() {
		return excludeIDs;
	}
		
}
