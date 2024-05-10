package com.sun.tools.xjc.addon.krasa;

import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.validation.Valid;
import javax.xml.namespace.QName;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.tools.common.ToolConstants;
import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.tools.common.model.JAnnotation;
import org.apache.cxf.tools.common.model.JavaInterface;
import org.apache.cxf.tools.common.model.JavaMethod;
import org.apache.cxf.tools.common.model.JavaModel;
import org.apache.cxf.tools.common.model.JavaParameter;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.SEIGenerator;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.processor.WSDLToJavaProcessor;

/**
 * Performs validation on fields annotated with {@link Valid} annotation.
 *
 * @author Vojtěch Krása
 */
public class ValidSEIGenerator extends SEIGenerator {

	private static final String VALID_PARAM = "VALID_PARAM";
	private static final String VALID_RETURN = "VALID_RETURN";
    private static final JAnnotation VALID_ANNOTATION_CLASS = new JAnnotation(Valid.class);

	private boolean validIn = true;
	private boolean validOut = true;

    @Override
	public String getName() {
		return "krasa";
	}

	@Override
	public void generate(ToolContext ctx) throws ToolException {
		parseArguments(ctx);

		Map<QName, JavaModel> map = CastUtils.cast((Map<?, ?>) ctx.get(WSDLToJavaProcessor.MODEL_MAP));
		for (JavaModel javaModel : map.values()) {
			Map<String, JavaInterface> interfaces = javaModel.getInterfaces();

			for (JavaInterface intf : interfaces.values()) {
				intf.addImport(Valid.class.getCanonicalName());

                List<JavaMethod> methods = intf.getMethods();
				for (JavaMethod method : methods) {
					List<JavaParameter> parameters = method.getParameters();
					if (validOut) {
						method.addAnnotation(VALID_RETURN, VALID_ANNOTATION_CLASS);
					}
					for (JavaParameter param : parameters) {
						if (validIn && (param.isIN() || param.isINOUT())) {
							param.addAnnotation(VALID_PARAM, VALID_ANNOTATION_CLASS);
						}
						if (validOut && (param.isOUT() || param.isINOUT())) {
							param.addAnnotation(VALID_RETURN, VALID_ANNOTATION_CLASS);
						}
					}
				}
			}
		}

		super.generate(ctx);
	}

	private void parseArguments(ToolContext ctx) {
        String[] xjcArgs = (String[]) ctx.get(ToolConstants.CFG_XJC_ARGS);
		if (xjcArgs != null) {
			for (String arg : xjcArgs) {
				String[] parts = arg.split("=");
				if (parts.length == 2 &&
                        parts[0].contains(ValidationsArgument.generateServiceValidationAnnotations.name())) {
					parseValidationPolicy(parts[1]);
				}
				LOG.log(Level.FINE, "xjc arg:" + arg);
			}
		}
	}

	public void parseValidationPolicy(String policy) {
		if ("in".equalsIgnoreCase(policy)) {
			validOut = false;
		} else if ("out".equalsIgnoreCase(policy)) {
			validIn = false;
		}
	}
}