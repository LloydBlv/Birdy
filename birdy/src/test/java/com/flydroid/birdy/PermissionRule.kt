package com.flydroid.birdy

import androidx.test.rule.GrantPermissionRule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class PermissionRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        val grantPermissions = description.getAnnotation(GrantPermissions::class.java)
        var result = base
        grantPermissions?.value?.forEach { permission ->
            result = GrantPermissionRule.grant(permission).apply(result, description)
        }
        return result
    }
}