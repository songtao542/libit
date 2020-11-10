package com.liabit.repackage


import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.quinn.hunter.transform.HunterTransform
import org.gradle.api.Project

class RepackageTransform extends HunterTransform {

    private RepackageExtension ext

    RepackageTransform(Project project, RepackageExtension extension) {
        super(project)
        this.ext = extension

        this.bytecodeWeaver = new ClassWeaver()

    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
    }

}