package com.lovelycatv.vertex.extension

/**
 * @author lovelycat
 * @since 2024-10-23 22:26
 * @version 1.0
 */
fun <R> Boolean?.runIfTrue(action: () -> R): R? = if (this != null && this) action() else null

fun <R> Boolean?.runIfFalse(action: () -> R): R? = if (this == null || !this) action() else null

suspend fun <R> Boolean?.runIfTrue(action: suspend () -> R): R? = if (this != null && this) action() else null

suspend fun <R> Boolean?.runIfFalse(action: suspend () -> R): R? = if (this == null || !this) action() else null