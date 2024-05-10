package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSimpleType;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Facet {
    private final XSSimpleType simpleType;

    public Facet(XSSimpleType simpleType) {
        this.simpleType = simpleType;
    }

    public boolean targetNamespaceEquals(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return false;
        }
        String elemNs = simpleType.getTargetNamespace();
        return elemNs.startsWith(namespace);
    }

    public Integer minLength() {
        return getIntegerFacet(XSFacet.FACET_MINLENGTH);
    }

    public Integer maxLength() {
        return getIntegerFacet(XSFacet.FACET_MAXLENGTH);
    }

    public Integer length() {
        return getIntegerFacet(XSFacet.FACET_LENGTH);
    }

    public Integer totalDigits() {
        return getIntegerFacet(XSFacet.FACET_TOTALDIGITS);
    }

    public Integer fractionDigits() {
        return getIntegerFacet(XSFacet.FACET_FRACTIONDIGITS);
    }

    public BigDecimal minInclusive() {
        return getDecimalFacet(XSFacet.FACET_MININCLUSIVE);
    }

    public BigDecimal minExclusive() {
        return getDecimalFacet(XSFacet.FACET_MINEXCLUSIVE);
    }

    public BigDecimal maxInclusive() {
        return getDecimalFacet(XSFacet.FACET_MAXINCLUSIVE);
    }

    public BigDecimal maxExclusive() {
        return getDecimalFacet(XSFacet.FACET_MAXEXCLUSIVE);
    }

    public String pattern() {
        return getStringFacet(XSFacet.FACET_PATTERN);
    }

    public List<String> patternList() {
        return getMultipleStringFacets(XSFacet.FACET_PATTERN);
    }

    public String enumeration() {
        return getStringFacet(XSFacet.FACET_ENUMERATION);
    }

    public List<String> enumerationList() {
        return getMultipleStringFacets(XSFacet.FACET_ENUMERATION);
    }

    private List<String> getMultipleStringFacets(String param) {
        final List<XSFacet> facets = simpleType.getFacets(param);
        if (facets != null) {
            return facets.stream()
                    .map(facet -> facet.getValue().value)
                    .filter(v -> v != null && !v.isEmpty())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private String getStringFacet(String param) {
        final XSFacet facet = simpleType.getFacet(param);
        return facet == null ? null : facet.getValue().value;
    }

    private Integer getIntegerFacet(String param) {
        final XSFacet facet = simpleType.getFacet(param);
        if (facet != null) {
            try {
                return Integer.parseInt(facet.getValue().value);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return null;
    }

    private BigDecimal getDecimalFacet(String param) {
        final XSFacet facet = simpleType.getFacet(param);
        if (facet != null) {
            final String str = facet.getValue().value;
            try {
                return new BigDecimal(str);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return null;
    }

}
