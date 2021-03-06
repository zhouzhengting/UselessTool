package com.yy.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.yy.plugin.extension.CheckMethodUsedExtension
import com.yy.plugin.utils.Logger
import com.yy.plugin.utils.MethodUsedCheckHelper
import org.gradle.api.Project

public class NoUsedMethodTransform extends com.android.build.api.transform.Transform {

    Project project
    MethodUsedCheckHelper mMethodUsedCheck;
    private int mNotUsedClassCount;
    private int mNotUsedMethodCount;
    private CheckMethodUsedExtension mCheckMethodUsedExension;

    @Override
    String getName() {
        return "noUsedMethod"
    }

    public NoUsedMethodTransform(Project project, CheckMethodUsedExtension checkMethodUsedExension) {
        this.project = project
        mCheckMethodUsedExension = checkMethodUsedExension;
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        Logger.i('noUsedMethod transform begin >>>.')
        mMethodUsedCheck = new MethodUsedCheckHelper(mCheckMethodUsedExension, project);
        initInjectClassPath();
        //遍历所有的class的所属文件夹，是查找的入口
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                int[] resultCount = mMethodUsedCheck.checkDir(directoryInput.file.absolutePath)
                mNotUsedClassCount += resultCount[0]
                mNotUsedMethodCount += resultCount[1]
            }
        }
        Logger.i("Not used class count = " + mNotUsedClassCount)
        Logger.i("Not used method count = " + mNotUsedMethodCount)
        //将最终查找到的无用类的数量和无用方法的数量保存到文件中
        mMethodUsedCheck.saveTotalCount(mNotUsedClassCount, mNotUsedMethodCount)
        throw new RuntimeException("check method end successfully")
    }

    /**
     * 为了将APP所依赖的各种jar包都加入到classpool中，如果不做此处理，会在使用javassist时抛出类无用找到的异常信息
     *
     * <br>不同app不一样。
     */
    private void initInjectClassPath() {
        project.android.bootClasspath.each {
            mMethodUsedCheck.addClassPath((String) it.absolutePath);
        }

//        //加入res modules下的依赖jar包
//        File dir = project.file("../res/noexportlibs")
//        if (dir != null && dir.isDirectory()) {
//            dir.eachFileRecurse { File file ->
//                String filePath = file.absolutePath;
//                if (filePath.endsWith(".jar")) {
//                    mMethodUsedCheck.addClassPath(filePath);
//                }
//            }
//        }

        //加入本工程下的依赖jar包
        File localDir = project.file("libs");
        if (localDir != null && localDir.isDirectory()) {
            localDir.eachFileRecurse { File file ->
                String filePath = file.absolutePath;
                if (filePath.endsWith(".jar")) {
                    mMethodUsedCheck.addClassPath(filePath);
                }
            }
        }
        //加入其它工程的jar包
        File otherModuleDir = project.file("build/intermediates/exploded-aar");
        if (otherModuleDir != null && otherModuleDir.isDirectory()) {
            otherModuleDir.eachFileRecurse { File file ->
                String filePath = file.absolutePath;
                if (filePath.endsWith(".jar")) {
                    mMethodUsedCheck.addClassPath(filePath);
                }
            }
        }
    }
}