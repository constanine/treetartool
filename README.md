# treetartool
树形数据
<pre>
-+- 1
 |    +- 100
 |    |  +- 10000
 |    |  +- 10001
 |    ....
 |    |  +- 10099
 |    +- 101
 |    |  +- 10100
 |    |  +- 10101
 |    ....
 |    |  +- 10199
 |    +- 102
 .....
 +- 9
 |    +- 900
 |    |  +- 90000
 |    |  +- 90001
 |    ....
 |    |  +- 90099
 |    +- 901
 |    |  +- 90100
 |    |  +- 90101
 |    ....
 |    |  +- 90199
 |    +- 902
</pre>
 
## 压缩/Compress
1. 给定目标数据集合 10001..10099,那么就可以压缩成 父节点ID:100,
2. 给定数据集合 10001... 19999 那么经过第一次压缩会到 100... 199,再次压缩就得到1
3. 给定数据集合 10001... 99999 那么经过第一次压缩会到 100... 199,再次压缩就得到1...9,那可以在压缩下等到全部节点(虚节点)0来表示

### 结合Exclude集合方案
给定的数据集合10001..10097,那么就可以压缩成 父节点ID:100,额外在Exclude集合出现 98,99,表示除了98,99其余兄弟节点全部在

## 扩展/extand
1. 给定数据集合 100,就可以扩展成 10000...10099
2. 给定数据集合 1, 就可以扩展成 10001... 19999

### 结合Exclude集合方案
给定的数据集合1,Exclude集合:98,99,就可以扩展成  10001..10097