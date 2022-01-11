/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.ir.visitors.IrThinVisitor

abstract class IrDynamicExpression : IrExpression()

abstract class IrDynamicOperatorExpression : IrDynamicExpression() {
    abstract val operator: IrDynamicOperator

    abstract var receiver: IrExpression

    abstract val arguments: MutableList<IrExpression>

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitDynamicOperatorExpression(this, data)

    override fun <R, D> accept(visitor: IrThinVisitor<R, D>, data: D): R =
        visitor.visitDynamicOperatorExpression(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        receiver.accept(visitor, data)
        for (valueArgument in arguments) {
            valueArgument.accept(visitor, data)
        }
    }

    override fun <D> acceptChildren(visitor: IrThinVisitor<Unit, D>, data: D) {
        receiver.accept(visitor, data)
        for (valueArgument in arguments) {
            valueArgument.accept(visitor, data)
        }
    }
}

var IrDynamicOperatorExpression.left: IrExpression
    get() = receiver
    set(value) {
        receiver = value
    }

var IrDynamicOperatorExpression.right: IrExpression
    get() = arguments[0]
    set(value) {
        if (arguments.isEmpty())
            arguments.add(value)
        else
            arguments[0] = value
    }

abstract class IrDynamicMemberExpression : IrDynamicExpression() {
    abstract val memberName: String

    abstract var receiver: IrExpression

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitDynamicMemberExpression(this, data)

    override fun <R, D> accept(visitor: IrThinVisitor<R, D>, data: D): R =
        visitor.visitDynamicMemberExpression(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        receiver.accept(visitor, data)
    }

    override fun <D> acceptChildren(visitor: IrThinVisitor<Unit, D>, data: D) {
        receiver.accept(visitor, data)
    }
}

enum class IrDynamicOperator(val image: String, val isAssignmentOperator: Boolean = false) {
    UNARY_PLUS("+"),
    UNARY_MINUS("-"),
    EXCL("!"),
    PREFIX_INCREMENT("++", isAssignmentOperator = true),
    POSTFIX_INCREMENT("++", isAssignmentOperator = true),
    PREFIX_DECREMENT("--", isAssignmentOperator = true),
    POSTFIX_DECREMENT("--", isAssignmentOperator = true),

    BINARY_PLUS("+"),
    BINARY_MINUS("-"),
    MUL("*"),
    DIV("/"),
    MOD("%"),
    GT(">"),
    LT("<"),
    GE(">="),
    LE("<="),
    EQEQ("=="),
    EXCLEQ("!="),
    EQEQEQ("==="),
    EXCLEQEQ("!=="),
    ANDAND("&&"),
    OROR("||"),

    EQ("=", isAssignmentOperator = true),
    PLUSEQ("+=", isAssignmentOperator = true),
    MINUSEQ("-=", isAssignmentOperator = true),
    MULEQ("*=", isAssignmentOperator = true),
    DIVEQ("/=", isAssignmentOperator = true),
    MODEQ("%=", isAssignmentOperator = true),

    ARRAY_ACCESS("[]"),
    INVOKE("()")
}
