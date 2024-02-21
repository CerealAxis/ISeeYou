package xaviermc.top.iseeyou.anticheat

import top.leavesmc.leaves.entity.Photographer

data class SuspiciousPhotographer(
    val photographer: Photographer,
    val name: String,
//    var matrixlastTagged: Long,
//    var themislastTagged: Long,
    var lastTagged: Long,
    val source: String? = null
)
