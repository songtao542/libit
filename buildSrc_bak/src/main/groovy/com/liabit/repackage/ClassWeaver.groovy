package com.liabit.repackage

import com.quinn.hunter.transform.asm.BaseWeaver

class ClassWeaver extends BaseWeaver {
    @Override
    boolean isWeavableClass(String fullQualifiedClassName) {
        println("fullQualifiedClassName====>" + fullQualifiedClassName)
        return super.isWeavableClass(fullQualifiedClassName)
    }

    @Override
    byte[] weaveSingleClassToByteArray(InputStream inputStream) throws IOException {
        println("inputStream====>" + inputStream)
        return super.weaveSingleClassToByteArray(inputStream)
    }
}