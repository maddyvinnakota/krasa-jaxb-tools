![Maven Central](https://img.shields.io/maven-central/v/com.fillumina/krasa-jaxb-tools.svg)

# XJC Plugin to generate Bean Validation Annotations 2.0 ([JSR-380](https://jcp.org/en/jsr/detail?id=380))

It works with (and possibly other plugins using `xjc`):

- [`maven-jaxb2-plugin`](https://github.com/highsource/jaxb-tools) see [krasa-maven-jaxb2-plugin-example](https://github.com/fillumina/krasa-jaxb-tools-example/blob/master/krasa-maven-jaxb2-plugin-example/pom.xml)
- [`jaxb2-maven-plugin`](https://github.com/mojohaus/jaxb2-maven-plugin) see [krasa-jaxb2-maven-plugin-example](https://github.com/fillumina/krasa-jaxb-tools-example/blob/master/krasa-jaxb2-maven-plugin-example/pom.xml)
- [`cxf-codegen-plugin`](https://cxf.apache.org/docs/maven-cxf-codegen-plugin-wsdl-to-java.html) see: [krasa-cxf-codegen-plugin-example](https://github.com/fillumina/krasa-jaxb-tools-example/blob/master/krasa-cxf-codegen-plugin-example/pom.xml)
- [`cxf-xjc-plugin`](https://cxf.apache.org/cxf-xjc-plugin.html) see [krasa-cxf-xjc-plugin-example](https://github.com/fillumina/krasa-jaxb-tools-example/blob/master/krasa-cxf-xjc-plugin-example/pom.xml)

Refer to the [`krasa-jaxb-tools-example`](https://github.com/fillumina/krasa-jaxb-tools-example) project for usage examples.

JDK 1.8 Support
----------------

The project is bounded to support **Java 8** (**JDK 1.8**) because of some old projects still requiring it. All dependencies are selected from the latest available versions still supporting that.

Check [krasa-issue13](https://github.com/fillumina/krasa-issue13) if you need an example of a project using `krasa-jaxb-tools` with `JDK 21`, `cxf-codegen-plugin` 4.0.4 and all the latest dependencies.

Version
----------------

- `2.3.5` fix critical vulnerabilities found in dependencies, see [Sonatype report](https://sbom.sonatype.com/report/T1-a4e79c5353879ed9b588-23af948b811c4e-1726254616-6f0f87d1e3be445d8022a8d5689bf3c5).

- `2.3.4` bug fix release:
  - fix [Issue #17](https://github.com/fillumina/krasa-jaxb-tools/issues/17) where `@DecimalMin` and `@DecimalMax` superfluous annotations were added to
    numeric java types. A new argument has been created `generateAllNumericConstraints` in case
    all constraints would be needed (even superflous ones).

- `2.3.3` bug fix release:

  - fix [Issue #13](https://github.com/fillumina/krasa-jaxb-tools/issues/13) where it's been wrongly assumed that:
    `SimpleTypeImpl particle = (SimpleTypeImpl) definition` was always true.
    A check has beed added to prevent the `ClassCastException`.

- `2.3.2` another bug fix release:

  - fix `@Pattern` added to wrong fields (regression from 2.2)
  - add `@EachPattern` when needed for `List<String>` fields
  - rename `generateStringListAnnotations` option to `generateListAnnotations` because it is not limited to list of strings
  - disable `generateListAnnotations` by default (was enabled)

- `2.3.1` bug fix release:

  - `@Valid` annotation was not added by default
  - remove `singlePattern` option because `@Pattern.List` is not semantically correct
  - disable `jpa` option because not really useful
  - disable `JSR_349` option it was referring to Validation API 1.1 while now we use 2.0
  - add a lot of tests to establish a solid baseline (defaults was backported and tested on 2.2)

- `2.3` A huge refactoring and bug fixing:

  - added `singlePattern` option
  - fixed `generateServiceValidationAnnotations` used by `ValidSEIGenerator` to accept string parameter
  - dependencies updated to the latest version still supporting JDK 1.8
  - a maven rule has been set to force compilation with JDK 1.8

- `2.2` Some new features added because of PR requests

  - Added `@Valid` annotation to `sequence`s to force items validation
  - Added support for `Jakarta EE 9` with parameter `validationAnnotations`

- `2.1` Revert back to Java 1.8 (sorry folks!).

- `2.0` A refactorized version of the original [krasa-jaxb-toos](https://github.com/krasa/krasa-jaxb-tools) last synced on August 2022, with some enhancements (support for `EachDigits`, `EachDecimalMin` and `EachDecimalMax` in primitive lists), improved tests and bug fixed. It is compiled using JDK 11. The `pom.xml` `groupId` has been changed to `com.fillumina`.

Release
----------------

```xml
<dependency>
    <groupId>com.fillumina</groupId>
    <artifactId>krasa-jaxb-tools</artifactId>
    <version>2.3.5</version>
</dependency>
```

Options
----------------

- `verbose` (boolean, default=`false`) print verbose messages to output
- `validationAnnotations` (`javax` | `jakarta`, default=`javax`): selects the library to use for annotations
- `targetNamespace` (string): adds @Valid annotation to all elements with given namespace
- `generateNotNullAnnotations` (boolean, default=`true`): adds a `@NotNull` annotation if an element has `minOccours` not 0, is `required` or is not `nillable`.
- `notNullAnnotationsCustomMessages` (boolean or string, default=`false`): values are `true`, `FieldName`, `ClassName`, or an *actual message* (see further explanation down below).
- `generateListAnnotations` (boolean, optional, default `false`) generates [validator-collection annotations](https://github.com/jirutka/validator-collection) annotations
- `generateServiceValidationAnnotations` (string, accepts: `in`, `out`, `inout`, works with  `apache-cxf` only) adds `@Valid` annotations to respective message direction (in, out or both).
- `generateAllNumericConstraints` (boolean, defaults to `false`) generates all `@DecimalMin` and `@DecimalMax` even those regarding the natural boudaries of the referred java type.

#### Notes

- Arguments accepting booleans can either be given `true` value as with `verbose=true` or simply be left without a value at all and that will be interpteted as being `true`  (you can omit the `=` too).

- All arguments are optional.

### `notNullAnnotationsCustomMessages` argument

**`@NotNull`**'s default validation message is not always helpful, so it can be customized with **-XJsr303Annotations:notNullAnnotationsCustomMessages=OPTION** where **OPTION** is one of the following:

- `false` default: no custom message
- `true` message is present but equivalent to the default: **"{javax.validation.constraints.NotNull.message}"**
- `FieldName` field name is prefixed to the default message: **"fieldName {javax.validation.constraints.NotNull.message}"**
- `ClassName` class and field name are prefixed to the default message: **"ClassName.fieldName {javax.validation.constraints.NotNull.message}"**
- `other-non-empty-text` arbitrary message, with substitutable, case-sensitive parameters `{ClassName}` and `{FieldName}`: **"Class {ClassName} field {FieldName} non-null"**

### `generateServiceValidationAnnotations` argument

Bean validation policy can be customized with `-XJsr303Annotations:generateServiceValidationAnnotations=OPTION` where OPTION is one of the following:

- `InOut` (default: validate requests and responses)
- `In` (validate only requests)
- `Out` (validate only responses)

Using this option requires to specify krasa as front end generator (See example in https://github.com/fillumina/krasa-jaxb-tools-example )

### `XReplacePrimitives` argument

That is a different plugin within this same packakge and replaces primitive types with boxed ones. It's enabled in the [krasa-cxf-codegen-plugin-example](https://github.com/fillumina/krasa-jaxb-tools-example/blob/master/krasa-cxf-codegen-plugin-example/pom.xml) project as an example.
**WARNING:** must be defined before XhashCode or Xequals.

XJsr303Annotations
----------------

Generates:

- `@Valid` annotation for all complex types, can be further restricted to generate only for types from defined schema: -XJsr303Annotations:targetNamespace=http://www.foo.com/bar
- `@NotNull` annotation for objects that has a MinOccur value >= 1 or for attributes with required use
- `@Size` for lists that have minOccurs > 1
- `@Size` if there is a maxLength or minLength or length restriction
- `@DecimalMax` for maxInclusive restriction
- `@DecimalMin` for minInclusive restriction
- `@DecimalMax` for maxExclusive restriction, enable new parameter (inclusive=false) with: -XJsr303Annotations:JSR_349=true
- `@DecimalMin` for minExclusive restriction, enable new parameter (inclusive=false) with: -XJsr303Annotations:JSR_349=true
- `@Digits` if there is a totalDigits or fractionDigits restriction.
- `@Pattern` and `@PatternList` if there is a Pattern restriction (see `singlePattern` option)

Example project with tests
----------------

This other project is maintained to allow testing and showcase usage:

https://github.com/fillumina/krasa-jaxb-tools-example

Note that the JDK 8 compatibility requirement impose serious restrictions on the dependency versions available so it is *highly advisable* to check the used versions and general usage carefully in this test project.
