package com.liabit.repackage

import com.android.build.api.dsl.extension.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class RepackagePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // repackage 在 build.gradle 中传值使用
        RepackageExtension extension = project.extensions.create("repackage",  RepackageExtension.class)
        println("------------Repackage--------------")
        println("{" + extension.regex + " -> " + extension.replacement + "}")
        println("------------Repackage--------------")
        println("------------------------------------- " + project.extensions)

        AppExtension appExtension = project.extensions.findByType(AppExtension.class)
        appExtension.registerTransform(new RepackageTransform(extension))
    }

//    class RepackageExtension {
//        String regex = ""
//        String replacement = ""
//    }
}

