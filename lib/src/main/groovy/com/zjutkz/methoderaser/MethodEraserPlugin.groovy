package com.zjutkz.methoderaser

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

/**
 * Created by kangzhe on 18/3/25.
 */

class MethodEraserPlugin implements Plugin<Project> {

    private MethodEraser eraser = new MethodEraser();
    @Override
    void apply(Project project) {
        project.extensions.create("eraserConfig",MethodEraserExtension.class)
        project.afterEvaluate {
            def variants
            if (project.plugins.hasPlugin(AppPlugin)) {
                AppExtension android = project.extensions.getByType(AppExtension.class)
                variants = android.getApplicationVariants()
            } else {
                LibraryExtension android = project.extensions.getByType(LibraryExtension.class)
                variants = android.getLibraryVariants()
            }

            def sdkDir
            Properties properties = new Properties()
            File local = project.rootProject.file('local.properties')
            if(local.exists()){
                properties.load(local.newDataInputStream())
            }
            if (System.getenv("ANDROID_HOME") != null) {
                sdkDir = System.getenv("ANDROID_HOME")
            } else {
                sdkDir = properties.getProperty('sdk.dir')
            }
            def androidJar = "${sdkDir}/platforms/${project.android.compileSdkVersion}/android.jar"
            eraser.appendPath(androidJar)

            variants.all { variant ->
                println("buildType: " + variant.getBuildType().name)
                if("release".equalsIgnoreCase(variant.getBuildType().name)){
                    JavaCompile javaCompile = (JavaCompile) (variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile)
                    javaCompile.doLast {
                        MethodEraserExtension extension = project.getExtensions().findByType(MethodEraserExtension.class)
                        eraser.shrinkMethod(javaCompile.getDestinationDir().toString(), extension.packageName.replaceAll(".", "/"))
                        eraser.releaseAllRes()
                    }
                }
            }
        }
    }
}
