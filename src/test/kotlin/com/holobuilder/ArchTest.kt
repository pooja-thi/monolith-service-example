package com.holobuilder

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.holobuilder")

        noClasses()
            .that()
            .resideInAnyPackage("com.holobuilder.service..")
            .or()
            .resideInAnyPackage("com.holobuilder.repository..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..com.holobuilder.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses)
    }
}
