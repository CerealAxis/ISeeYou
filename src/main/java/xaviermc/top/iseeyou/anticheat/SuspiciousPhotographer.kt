package xaviermc.top.iseeyou.anticheat

import top.leavesmc.leaves.entity.Photographer

data class SuspiciousPhotographer(
    val photographer: Photographer,
    val name: String,
    val lastTagged: Long,
    val source: String? = null
)
