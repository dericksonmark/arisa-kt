package io.github.mojira.arisa.modules

import arrow.core.left
import arrow.core.right
import io.kotlintest.assertions.arrow.either.shouldBeLeft
import io.kotlintest.assertions.arrow.either.shouldBeRight
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class CHKModuleTest : StringSpec({
    "should not process tickets without a confirmation status" {
        val module = CHKModule { Unit.right() }
        val request = CHKModuleRequest("issue", null, null)

        val result = module(request)

        result.shouldBeLeft(OperationNotNeededModuleResponse)
    }

    "should not process tickets with a Undefined confirmation status" {
        val module = CHKModule { Unit.right() }
        val request = CHKModuleRequest("issue", null, "Undefined")

        val result = module(request)

        result.shouldBeLeft(OperationNotNeededModuleResponse)
    }

    "should not process tickets with CHK already set" {
        val module = CHKModule { Unit.right() }
        val request = CHKModuleRequest("issue", "chkField", "Confirmed")

        val result = module(request)

        result.shouldBeLeft(OperationNotNeededModuleResponse)
    }

    "should process confirmed tickets" {
        val module = CHKModule { Unit.right() }
        val request = CHKModuleRequest("issue", null, "Confirmed")

        val result = module(request)

        result.shouldBeRight(ModuleResponse)
    }

    "should return FailedModuleResponse when update fails" {
        val module = CHKModule { RuntimeException().left() }
        val request = CHKModuleRequest("issue", null, "Confirmed")

        val result = module(request)

        result.shouldBeLeft()
        result.a should { it is FailedModuleResponse }
        (result.a as FailedModuleResponse).exceptions.size shouldBe 1
    }
})
