package com.liabit.citypicker

class LocatedCity(private val section: String, name: String, province: String, py: String) : City(name, province, "0", py) {

    override fun getItemSection(): String {
        return section
    }

}
