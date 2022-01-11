/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.ir.visitors.IrThinVisitor

abstract class IrConstantValue : IrExpression() {
    abstract fun contentEquals(other: IrConstantValue) : Boolean
    abstract fun contentHashCode(): Int
}

abstract class IrConstantPrimitive : IrConstantValue() {
    abstract var value: IrConst<*>

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitConstantPrimitive(this, data)

    override fun <R, D> accept(visitor: IrThinVisitor<R, D>, data: D): R =
        visitor.visitConstantPrimitive(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        value.accept(visitor, data)
    }

    override fun <D> acceptChildren(visitor: IrThinVisitor<Unit, D>, data: D) {
        value.accept(visitor, data)
    }
}

abstract class IrConstantObject : IrConstantValue() {
    abstract val constructor: IrConstructorSymbol
    abstract val valueArguments: List<IrConstantValue>
    abstract val typeArguments: List<IrType>
    abstract fun putArgument(index: Int, value: IrConstantValue)

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitConstantObject(this, data)

    override fun <R, D> accept(visitor: IrThinVisitor<R, D>, data: D): R =
        visitor.visitConstantObject(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        valueArguments.forEach { value -> value.accept(visitor, data) }
    }

    override fun <D> acceptChildren(visitor: IrThinVisitor<Unit, D>, data: D) {
        valueArguments.forEach { value -> value.accept(visitor, data) }
    }
}

abstract class IrConstantArray : IrConstantValue() {
    abstract val elements: List<IrConstantValue>
    abstract fun putElement(index: Int, value: IrConstantValue)

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitConstantArray(this, data)

    override fun <R, D> accept(visitor: IrThinVisitor<R, D>, data: D): R =
        visitor.visitConstantArray(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        elements.forEach { value -> value.accept(visitor, data) }
    }

    override fun <D> acceptChildren(visitor: IrThinVisitor<Unit, D>, data: D) {
        elements.forEach { value -> value.accept(visitor, data) }
    }
}
