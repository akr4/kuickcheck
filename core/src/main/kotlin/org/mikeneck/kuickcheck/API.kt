/*
 * Copyright 2016 Shinya Mochida
 * 
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.kuickcheck

import org.mikeneck.kuickcheck.generator.IntGenerator
import org.mikeneck.kuickcheck.generator.LongGenerator
import org.mikeneck.kuickcheck.prediction.SingleParameterPrediction
import org.mikeneck.kuickcheck.prediction.singleParameterPrediction
import org.mikeneck.kuickcheck.runner.ClassScanner
import org.mikeneck.kuickcheck.runner.toSummary
import kotlin.system.exitProcess

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Property

interface Checker<T> {
    fun testData(): T
    fun consume(p: T): Boolean
    val repeat: Int

    infix operator fun times(rep: Int): Checker<T> {
        val checker = this
        return object: Checker<T> {
            override fun testData(): T = checker.testData()
            override fun consume(p: T): Boolean = checker.consume(p)
            override val repeat: Int = rep
        }
    }
}

interface Checker2<F, S>: Checker<Pair<F, S>>

interface Generator<out T> {
    fun generate(): T
}

object KuickCheck {

    @JvmStatic fun main(args: Array<String>) {
        if (args.contains("--debug")) ClassScanner.debug()
        val properties = ClassScanner.prepareForCheck()
        println("${properties.size} properties found.")
        val checksToRun = properties.map { it.getExecutor() }
        val summary = checksToRun.map { it.run() }.toSummary()

        summary.printResult()
        summary.printSummary()
        exitProcess(if (summary.testSuccess()) 0 else 1)
    }
}

fun <T> forAll(generator: Generator<T>): SingleParameterPrediction<T> = singleParameterPrediction(generator)

val int: IntGenerator = IntGenerator(Int.MIN_VALUE, Int.MAX_VALUE)

val positiveInt: IntGenerator = IntGenerator(1, Int.MAX_VALUE)

val positiveIntFrom0: IntGenerator = IntGenerator(0, Int.MAX_VALUE)

val negativeInt: IntGenerator = IntGenerator(Int.MIN_VALUE, -1)

val negativeIntTo0: IntGenerator = IntGenerator(Int.MIN_VALUE, 0)

fun int(min: Int, max: Int): IntGenerator = IntGenerator(min, max)

val long: LongGenerator = LongGenerator(Long.MIN_VALUE, Long.MAX_VALUE)

val positiveLong: LongGenerator = LongGenerator(1, Long.MAX_VALUE)

val positiveLongFrom0: LongGenerator = LongGenerator(0, Long.MAX_VALUE)

val negativeLong: LongGenerator = LongGenerator(Long.MIN_VALUE, -1)

val negativeLongTo0: LongGenerator = LongGenerator(Long.MIN_VALUE, 0)

fun long(min: Long, max: Long): LongGenerator = LongGenerator(min, max)
