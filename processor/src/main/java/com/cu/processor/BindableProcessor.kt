package com.cu.processor

import com.cu.annotation.Compositable
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy


@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(BindableProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class BindableProcessor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        println("getSupportedAnnotationTypes")
        return mutableSetOf(Compositable::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        println("process")
        roundEnv.getElementsAnnotatedWith(Compositable::class.java)
            .forEach {
                val className = it.simpleName.toString()
                println("Processing: $className")
                val pack = processingEnv.elementUtils.getPackageOf(it).toString()
                val viewId = it.getAnnotation(Compositable::class.java).viewId
                generateClass(className, pack, viewId)
            }
        return true
    }

    private fun generateClass(className: String, pack: String, viewId: Int) {
        val fileName = "${className}CU"

        val compositeUnit = ClassName("com.example.compositeunit2.base", "CompositeUnit")
        val source = ClassName(pack, className)

        val layoutIdProperty = PropertySpec.builder("layoutId", Int::class.java)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%L", viewId)
            .build()


        val clazzClass = ClassName("java.lang", "Class")


        val clazzProperty = PropertySpec.builder("clazz", clazzClass.parameterizedBy(source))
            .addModifiers(KModifier.OVERRIDE)
            .initializer("%L::class.java", source.simpleName)
            .build()

        val file = FileSpec.builder(pack, fileName)
            .addType(
                TypeSpec.classBuilder(fileName)
                    .addSuperinterface(compositeUnit)
                    .addProperty(layoutIdProperty)
                    .addProperty(clazzProperty)
                    .build()
            )
            .build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir, "$fileName.kt"))
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}
