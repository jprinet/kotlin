/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrEnumEntrySymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.ir.visitors.IrThinVisitor

abstract class IrDeclarationReference : IrExpression() {
    abstract val symbol: IrSymbol
}

abstract class IrGetSingletonValue : IrDeclarationReference()

abstract class IrGetObjectValue : IrGetSingletonValue() {
    abstract override val symbol: IrClassSymbol

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitGetObjectValue(this, data)

    override fun <R, D> accept(visitor: IrThinVisitor<R, D>, data: D): R =
        visitor.visitGetObjectValue(this, data)
}

abstract class IrGetEnumValue : IrGetSingletonValue() {
    abstract override val symbol: IrEnumEntrySymbol

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitGetEnumValue(this, data)

    override fun <R, D> accept(visitor: IrThinVisitor<R, D>, data: D): R =
        visitor.visitGetEnumValue(this, data)
}

/**
 * Platform-specific low-level reference to function.
 *
 * On JS platform it represents a plain reference to JavaScript function.
 * On JVM platform it represents a MethodHandle constant.
 */
abstract class IrRawFunctionReference : IrDeclarationReference() {
    abstract override val symbol: IrFunctionSymbol

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitRawFunctionReference(this, data)

    override fun <R, D> accept(visitor: IrThinVisitor<R, D>, data: D): R =
        visitor.visitRawFunctionReference(this, data)
}
