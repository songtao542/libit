package com.liabit.squareflag

data class Country(
    val name: String,
    val alpha3: String,
    val alpha2: String,
    val dialingCode: String,
    var flag: Int,
) : Comparable<Country> {
    override fun compareTo(other: Country): Int {
        return name.compareTo(other.name)
    }
}
