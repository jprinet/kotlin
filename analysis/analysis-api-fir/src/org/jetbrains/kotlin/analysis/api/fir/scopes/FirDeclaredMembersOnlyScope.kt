/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.fir.scopes

import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.declarations.utils.classId
import org.jetbrains.kotlin.fir.resolve.substitution.ConeSubstitutor
import org.jetbrains.kotlin.fir.scopes.*
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames

internal class FirDeclaredMembersOnlyScope(
    private val delegate: FirContainingNamesAwareScope,
    private val owner: FirClass,
) : FirCallableFilteringScope(delegate) {
    private fun FirCallableDeclaration.isDeclared(): Boolean =
        symbol.callableId.classId == owner.classId
                && origin !is FirDeclarationOrigin.SubstitutionOverride
                && origin != FirDeclarationOrigin.IntersectionOverride

    override fun isTargetCallable(callable: FirCallableSymbol<*>): Boolean =
        callable.callableId.callableName != SpecialNames.INIT && callable.fir.isDeclared()

    override fun processDeclaredConstructors(processor: (FirConstructorSymbol) -> Unit) {
        delegate.processDeclaredConstructors(processor)
    }

    override fun getClassifierNames(): Set<Name> = delegate.getClassifierNames()

    override fun processClassifiersByNameWithSubstitution(name: Name, processor: (FirClassifierSymbol<*>, ConeSubstitutor) -> Unit) {
        // We do not need to filter classifiers, because for both Kotlin and Java classes, a static base scope cannot contain classifiers
        // inherited from supertypes. See `getStaticMemberScope` in `KtScopeProvider` for more information on classifiers in static scopes.
        delegate.processClassifiersByNameWithSubstitution(name, processor)
    }

    override fun toString(): String = "Declared member scope for $delegate with owning class `${owner.classId}`"
}
