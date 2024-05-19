![Maven Central](https://img.shields.io/maven-central/v/com.fillumina/krasa-jaxb-tools.svg)

# Plugin to generate Bean Validation Annotations ([JSR-303](https://beanvalidation.org/1.0/spec/))

It works with (and possibly other plugins using `xjc`):

- [`maven-jaxb2-plugin`](https://github.com/highsource/jaxb-tools)
- [`jaxb2-maven-plugin`](https://github.com/mojohaus/jaxb2-maven-plugin)
- [`cxf-codegen-plugin`](https://cxf.apache.org/docs/maven-cxf-codegen-plugin-wsdl-to-java.html)
- [`cxf-xjc-plugin`](https://cxf.apache.org/cxf-xjc-plugin.html)

Refer to the [`krasa-jaxb-tools-example`](https://github.com/fillumina/krasa-jaxb-tools-example) project for usage examples.



## JDK 1.8 Support

The project is bounded to support **Java 8** (**JDK 1.8**) because of some old projects still requiring it. All dependencies are selected from the latest available versions still supporting that.

## Version

- `2.3.1` bug fixing release:

  - `@Valid` annotation was not added by default anymore

  - removed `singlePattern` option because `@Pattern.List` is not semantically correct

  - `jpa` option disabled because not really useful

  - `JSR_349` option disabled because it was referring to Validation API 1.1 while now we use 2.0

  - a lot of tests added to establish a solid baseline (defaults was backported and tested on 2.2)

- `2.3` A huge refactoring and fixing bugs:

  - added `singlePattern` option

  - fixed `generateServiceValidationAnnotations` used by `ValidSEIGenerator` to accept string parameter

  - dependencies updated to the latest version still supporting JDK 1.8

  - a maven rule has been set to force compilation with JDK 1.8

- `2.2` Some new features added because of PR requests

  - Added `@Valid` annotation to `sequence`s to force items validation
  - Added support for `Jakarta EE 9` with parameter `validationAnnotations`

- `2.1` Revert back to Java 1.8 (sorry folks!).

- `2.0` A refactorized version of the original [krasa-jaxb-toos](https://github.com/krasa/krasa-jaxb-tools) last synced on August 2022, with some enhancements (support for `EachDigits`, `EachDecimalMin` and `EachDecimalMax` in primitive lists), improved tests and bug fixed. It is compiled using JDK 11. The `pom.xml` `groupId` has been changed to `com.fillumina`.

-----

Release
----------------

```xml
<dependency>
    <groupId>com.fillumina</groupId>
    <artifactId>krasa-jaxb-tools</artifactId>
    <version>2.3.1</version>
</dependency>
```

Options
----------------

- `verbose` (boolean, optional, default=`false`) print verbose messages to output
- `singlePattern`  uses a single javax validation `@Pattern(A|B)` instead of `@Pattern.List( @Pattern(A), @Pattern(B) )`
- `validationAnnotations` (`javax` | `jakarta`, optional, default=`javax`): selects the library to use for annotations
- `targetNamespace` (string, optional): adds @Valid annotation to all elements with given namespace
- `generateNotNullAnnotations` (boolean, optional, default=`true`): adds a `@NotNull` annotation if an element has `minOccours` not 0, is `required` or is not `nillable`.
- `notNullAnnotationsCustomMessages` (boolean or string, optional, default=`false`): values are `true`, `FieldName`, `ClassName`, or an *actual message* (see further explanation down below).
- `JSR_349` (boolean, optional, defalut=`false`) generates [JSR349](https://beanvalidation.org/1.1/) compatible annotations for `@DecimalMax` and `@DecimalMin` inclusive parameter
- `jpa` (boolean, optional, default `false`) adds JPA `@Column` annotation for fields with multiplicity greater than 0
- `generateStringListAnnotations` (boolean, optional, default `false`) generates [validator-collection annotations](https://github.com/jirutka/validator-collection annotations)
- `generateServiceValidationAnnotations` (string, accepts: `in`, `out`, `inout`, works with  `apache-cxf` only) adds `@Valid` annotations to respective message direction (in, out or both).

----

**`@NotNull`**'s default validation message is not always helpful, so it can be customized with **-XJsr303Annotations:notNullAnnotationsCustomMessages=OPTION** where **OPTION** is one of the following:

* `false` default: no custom message
* `true` message is present but equivalent to the default: **"{javax.validation.constraints.NotNull.message}"**
* `FieldName` field name is prefixed to the default message: **"fieldName {javax.validation.constraints.NotNull.message}"**
* `ClassName` class and field name are prefixed to the default message: **"ClassName.fieldName {javax.validation.constraints.NotNull.message}"**
* `other-non-empty-text` arbitrary message, with substitutable, case-sensitive parameters `{ClassName}` and `{FieldName}`: **"Class {ClassName} field {FieldName} non-null"**

----

XJsr303Annotations
----------------

Generates:

* `@Valid` annotation for all complex types, can be further restricted to generate only for types from defined schema: -XJsr303Annotations:targetNamespace=http://www.foo.com/bar
* `@NotNull` annotation for objects that has a MinOccur value >= 1 or for attributes with required use
* `@Size` for lists that have minOccurs > 1
* `@Size` if there is a maxLength or minLength or length restriction
* `@DecimalMax` for maxInclusive restriction
* `@DecimalMin` for minInclusive restriction
* `@DecimalMax` for maxExclusive restriction, enable new parameter (inclusive=false) with: -XJsr303Annotations:JSR_349=true
* `@DecimalMin` for minExclusive restriction, enable new parameter (inclusive=false) with: -XJsr303Annotations:JSR_349=true
* `@Digits` if there is a totalDigits or fractionDigits restriction.
* `@Pattern` and `@PatternList` if there is a Pattern restriction (see `singlePattern` option)

----

Example project with tests
----------------

This other project is maintained to allow testing and showcase usage:

https://github.com/fillumina/krasa-jaxb-tools-example

Note that the JDK 8 compatibility requirement impose serious restrictions on the dependency versions available so it is *highly advisable* to check the used versions and general usage carefully in this test project.
