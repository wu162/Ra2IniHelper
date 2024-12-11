package com.github.wu162.ra2inihelper.lang

import com.github.wu162.ra2inihelper.lang.psi.IniProperty
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getKey
import com.github.wu162.ra2inihelper.lang.util.IniUtil
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext


class IniCompletionContributor: CompletionContributor() {

    companion object {

        val objectWeaponProp = listOf(
            "Primary",
            "Secondary",
            "DeathWeapon",
            "ElitePrimary",
            "EliteSecondary",
        )
    }

    init {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(IniTypes.VALUE), object : CompletionProvider<CompletionParameters>() {
            override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
                val position = parameters.position
                val property = position.parentOfType<IniProperty>(true) ?: return

                if (IniUtil.isObjectSection(property)) {
                    //在单位区块内
                    val key = property.getKey()

                    //武器自动提示
                    if (objectWeaponProp.contains(key)) {
                        resultSet.addAllElements(IniUtil.getAllWeapons(position.project).map { LookupElementBuilder.create(it) })
                    }

                }

            }

        })

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(IniTypes.KEY), object : CompletionProvider<CompletionParameters>() {
            override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
                val position = parameters.position
                val property = position.parentOfType<IniProperty>(true) ?: return

                if (IniUtil.isAircraftTypesSection(property)) {
                    resultSet.addAllElements(IniDocumentationProvider.AircraftTypeProps.map { LookupElementBuilder.create(it) })
                } else if (IniUtil.isBuildingTypesSection(property)) {
                    resultSet.addAllElements(IniDocumentationProvider.BuildingTypeProps.map { LookupElementBuilder.create(it) })
                } else if (IniUtil.isInfantryTypesSection(property)) {
                    resultSet.addAllElements(IniDocumentationProvider.InfantryTypeProps.map { LookupElementBuilder.create(it) })
                } else if (IniUtil.isVehicleTypesSection(property)) {
                    resultSet.addAllElements(IniDocumentationProvider.VehicleTypeProps.map { LookupElementBuilder.create(it) })
                }

            }

        })


        //属性名字提示  但需要知道有哪些属性  比如物体可以写哪些属性

    }



}