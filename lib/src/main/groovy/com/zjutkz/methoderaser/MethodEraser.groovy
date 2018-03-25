package com.zjutkz.methoderaser

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod;

/**
 * Created by kangzhe on 18/3/25.
 */

class MethodEraser {

    private static final String ERASER_ANNOTATION_NAME = "@com.zjutkz.methoderaser.Eraser"

    private ClassPool pool

    void appendPath(String path){
        checkNull();
        pool.appendClassPath(path)
    }

    void checkNull() {
        if(pool == null) {
            pool = new ClassPool()
        }
    }

    void shrinkMethod(String path, String packageName) {
        appendPath(path)
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                if (isClassFile(filePath)) {

                    println(filePath)
                    int index = filePath.indexOf(packageName);
                    int end = filePath.length() - 6
                    String className = filePath.substring(index, end).replace('\\', '.').replace('/', '.')
                    CtClass c = pool.getCtClass(className)
                    if (c.isFrozen()) {
                        c.defrost()
                    }
                    CtMethod[] methods = c.getMethods()
                    for(CtMethod method : methods) {
                        Object[] annotations = method.getAnnotations()
                        for (Object annotation : annotations) {
                            if (annotation.toString() == ERASER_ANNOTATION_NAME) {
                                doShrink(path,c,method)
                            }
                        }
                    }
                }
            }
        }
    }

    void releaseAllRes() {
        pool.clearImportedPackages()
        pool = null
    }

    private static boolean isClassFile(String filePath) {
        return filePath.endsWith(".class") &&
                !filePath.contains('R$') &&
                !filePath.contains('R.class') &&
                !filePath.contains("BuildConfig.class")
    }

    private static void doShrink(String path, CtClass clz, CtMethod method) {
        String type = method.getReturnType().getName()
        String body = "return null;"
        switch (type) {
            case "int":
            case "Integer":
                body = "return 1;"
                break
            case "double":
            case "Double":
                body = "return (double)1;"
                break
            case "long":
            case "Long":
                body = "return (long)1"
                break
            case "float":
            case "Float":
                body = "return (float)1"
                break
        }
        method.setBody(body)
        clz.writeFile(path)
        clz.detach()
    }
}
