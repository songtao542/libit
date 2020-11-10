package com.liabit.repackage

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class RepackagePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // repackage 在 build.gradle 中传值使用
        RepackageExtension extension = project.extensions.create("repackage", RepackageExtension.class)
        println("RepackageExtension{" + extension.regex + " -> " + extension.replacement + "}")

        BaseExtension androidExtension = project.getProperties().get("android")
        //BaseExtension appExtension = project.getExtensions().findByName("android")
        if (androidExtension != null) {
            androidExtension.registerTransform(new RepackageTransform(project, extension))
        }
    }
}

