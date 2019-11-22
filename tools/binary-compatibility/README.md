# Kotlin Public API binary compatibility validation tool

This tool allows to dump binary API of a Kotlin library that is public in sense of Kotlin visibilities and ensure that the public binary API wasn't changed in a way that make this change binary incompatible.

After you did some changes in code, please run `./gradlew tools:binary-compatibility:test`.

Copy-pasted from [Jetbrains/kotlin](https://github.com/JetBrains/kotlin/tree/master/libraries/tools/binary-compatibility-validator) repository.
So please, update it sometimes. 
Current version: 24 Oct 2019.

Kotlin is distributed under the terms of the Apache License (Version 2.0).
