/*START HEADER*/
/* Zofar Survey System
* Copyright (C) 2014 Deutsches Zentrum für Hochschul- und Wissenschaftsforschung
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
/*STOP HEADER*/
package eu.dzhw.zofar.testing.condition.term;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import eu.dzhw.zofar.testing.condition.term.elements.Element;
import eu.dzhw.zofar.testing.condition.term.elements.ElementVector;
public class TermAnalyzerClient implements Serializable {
	private static final long serialVersionUID = 3105933797437419268L;
	private final static TermAnalyzerClient INSTANCE = new TermAnalyzerClient();
	private TermAnalyzerClient() {
		super();
	}
	public static TermAnalyzerClient getInstance() {
		return INSTANCE;
	}
	public ElementVector mergeVectors(final ElementVector individual, final ElementVector initial) {
		final Map<String, Set<Element>> individualTupels = individual.getTupels();
		final Map<String, Set<Element>> initialTupels = initial.getTupels();
		if (initialTupels != null) {
			for (Map.Entry<String, Set<Element>> initialTupel : initialTupels.entrySet()) {
				if (!individualTupels.containsKey(initialTupel.getKey())) {
					individualTupels.put(initialTupel.getKey(), initialTupel.getValue());
				}
			}
		}
		final ElementVector back = new ElementVector();
		back.setTupels(individualTupels);
		return back;
	}
	public Map<String, ElementVector> analyze(final String expression, final Map<String, Object> contextData,
			final ElementVector initial,final boolean escalateExceptions) throws Exception {
		final Map<String, ElementVector> container = new LinkedHashMap<String, ElementVector>();
		final StandardEvaluationContext evaluationContext = new StandardEvaluationContext() {
		};
		final PropertyAccessor accessor = new PropertyAccessor() {
			public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
				return true;
			}
			public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
				boolean debug=false;
				debug=true;
				if (target != null) {
					try {
						final Field field = target.getClass().getDeclaredField(name);
						field.setAccessible(true);
						final Object value = field.get(target);
						final TypedValue back = new TypedValue(value);
						return back;
					} catch (Exception e) {
						throw new AccessException(e.getMessage(),e);
					}
				} else {
					Object contextItem = null;
					if (name.equals("zofar"))contextItem = contextData.get(name);
					if (name.equals("navigatorBean"))contextItem = contextData.get(name);
					if (name.equals("sessionController"))contextItem = contextData.get(name);
					if (name.equals("sessionController.participant"))contextItem = contextData.get(name);
					if(contextItem == null) {
						final Set<Element> possibleValues = (initial.getTupels().get(name));
						if((possibleValues != null)&&(!possibleValues.isEmpty())) {
							contextItem = possibleValues.toArray()[possibleValues.size()-1];
						}
					}
					final TypedValue back = new TypedValue(contextItem);
					return back;
				}
			}
			public Class<?>[] getSpecificTargetClasses() {
				return null;
			}
			public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
				return true;
			}
			public void write(EvaluationContext context, Object target, String name, Object newValue)
					throws AccessException {
			}
		};
		evaluationContext.addPropertyAccessor(accessor);
		final ExpressionParser parser = new SpelExpressionParser() {
			public Expression parseExpression(String expressionString, ParserContext context) throws ParseException {
				final SpelExpression back = (SpelExpression) super.parseExpression(expressionString, context);
				final SpelNode ast = back.getAST();
				try {
					walkAst(ast, initial, container, new HashMap<SpelNode, SpelNode>(), evaluationContext);
				} catch (Exception e) {
					boolean flag = false;
					if(e.getMessage().contains("no Tupel found for index_json_map.["))flag = true;
					if(e.getMessage().contains("no Tupel found for softforce.value"))flag = true;
					if(e.getMessage().contains("no Tupel found for monthpicker.value"))flag = true;
					if(e.getMessage().contains("no Tupel found for monthpicker.value"))flag = true;
					if(e.getMessage().contains("no Tupel found for month_box.value"))flag = true;
					if(e.getMessage().contains("no Tupel found for year_box.value"))flag = true;
					if(!flag)throw new ParseException(0, e.getMessage(), e);
				}
				return back;
			}
		};
		try {
			final Expression exp = parser.parseExpression(expression);
			final Object message = (Object) exp.getValue(evaluationContext);
		} catch (Exception e) {
			if ((org.springframework.expression.ParseException.class).isAssignableFrom(e.getClass())) {
				if(escalateExceptions)throw e;
				else {
					boolean show = true;
					final String msg = e.getMessage();
					if(msg.contains("For input string: \"null\" For input string: \"null\""))show = false;
					if(show) {
						System.err.println(e.getMessage());
					}
				}
			}
			else if ((java.lang.NullPointerException.class).isAssignableFrom(e.getClass())) {
			}
			else if ((org.springframework.expression.spel.SpelEvaluationException.class).isAssignableFrom(e.getClass())) {
				boolean show = true;
				final String msg = e.getMessage();
				if(msg.contains("Method call: Attempted to call method add"))show = false;
				if(msg.contains("Method call: Attempted to call method put"))show = false;
				if(msg.contains("Method call: Attempted to call method concat"))show = false;
				if(msg.contains("Method call: Attempted to call method size()"))show = false;
				if(msg.contains("Method concat(com.google.gson.JsonArray) cannot be found"))show = false;
				if(msg.contains("EL1012E:(pos 6): Cannot index into a null value"))show = false;
				if(msg.contains("The operator 'SUBTRACT' is not supported between objects of type 'null' and 'java.lang.Integer'"))show = false;
				if(show) {
					System.err.println(e.getMessage().replaceAll(Pattern.quote("EL1004E:(pos 6): Method call:"), ""));
				}
			}
			else {
				System.err.println("zz ["+expression+"]");
				System.err.println(e+" "+e.getMessage());
				e.printStackTrace();
			}
		}
		return container;
	}
	public Map<String, ElementVector> analyzeGraph(final String expression, final Map<String, Object> contextData,
			final ElementVector initial,final boolean escalateExceptions,final Map<String, Class> types) throws Exception {
		final Map<String, ElementVector> container = new LinkedHashMap<String, ElementVector>();
		final StandardEvaluationContext evaluationContext = new StandardEvaluationContext() {
		};
		final PropertyAccessor accessor = new PropertyAccessor() {
			public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
				return true;
			}
			public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
				if (target != null) {
					try {
						final Field field = target.getClass().getDeclaredField(name);
						field.setAccessible(true);
						final Object value = field.get(target);
						final TypedValue back = new TypedValue(value);
						return back;
					} catch (Exception e) {
						e.printStackTrace();
						throw new AccessException(e.getMessage(),e);
					}
				} else {
					Object contextItem = null;
					if (name.equals("zofar"))contextItem = contextData.get(name);
					if (name.equals("navigatorBean"))contextItem = contextData.get(name);
					if (name.equals("sessionController"))contextItem = contextData.get(name);
					if (name.equals("sessionController.participant"))contextItem = contextData.get(name);
					final TypedValue back = new TypedValue(contextItem);
					return back;
				}
			}
			public Class<?>[] getSpecificTargetClasses() {
				return null;
			}
			public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
				return true;
			}
			public void write(EvaluationContext context, Object target, String name, Object newValue)
					throws AccessException {
			}
		};
		evaluationContext.addPropertyAccessor(accessor);
		final ExpressionParser parser = new SpelExpressionParser() {
			public Expression parseExpression(String expressionString, ParserContext context) throws ParseException {
				final SpelExpression back = (SpelExpression) super.parseExpression(expressionString, context);
				final SpelNode ast = back.getAST();
				try {
					walkAst(ast, initial, container, new HashMap<SpelNode, SpelNode>(), evaluationContext);
				} catch (Exception e) {
					if(e.getMessage().startsWith("no Tupel found for ")) {
						final String tupel = e.getMessage().substring(19);
						final String var = tupel.replaceAll(Pattern.quote(".value"),"");
						Set<Element> options = new LinkedHashSet<Element>();
						if(types.containsKey(var)) {
							final Class typeClass = types.get(var);
							if((Boolean.class).isAssignableFrom(typeClass)) {
								options.add(new Element(true));
								options.add(new Element(false));
							}
							else if((Integer.class).isAssignableFrom(typeClass)) {
								for(int a=-3000; a <= +3000;a++)
									options.add(new Element(a));
							}
							else if((Double.class).isAssignableFrom(typeClass)) {
								for(int a=-3000; a <= +3000;a++)
									options.add(new Element(Double.valueOf(a+"D")));
							}
							else if((Float.class).isAssignableFrom(typeClass)) {
								for(int a=-3000; a <= +3000;a++)
									options.add(new Element(Float.valueOf(a+"F")));
							}
							if((String.class).isAssignableFrom(typeClass)) {
								options.add(new Element("1"));
							}
						}
						else {
							options.add(new Element("1"));
						}
						initial.getTupels().put(var, options);
						initial.getTupels().put(tupel, options);
						return parseExpression(expressionString,context);
					}
					else if(e.getMessage().startsWith("No equal Value found {}")) {
					}
					else throw new ParseException(0, e.getMessage(), e);
				}
				return back;
			}
		};
		try {
			final Expression exp = parser.parseExpression(expression);
			final Object message = (Object) exp.getValue(evaluationContext);
		} catch (Exception e) {
			if ((org.springframework.expression.ParseException.class).isAssignableFrom(e.getClass())) {
				if(escalateExceptions)throw e;
				else {
					System.err.println("["+expression+"] ==> "+e.getMessage());
					e.printStackTrace();
				}
			}
			else e.printStackTrace();
		}
		return container;
	}
	private static void walkAst(SpelNode node, ElementVector initial, Map<String, ElementVector> container,
			final Map<SpelNode, SpelNode> parentMap, EvaluationContext context) throws Exception {
		walkAst(node, true, initial, container, parentMap, context);
	}
	private static void walkAst(SpelNode node, final boolean first, ElementVector initial,
			Map<String, ElementVector> container, final Map<SpelNode, SpelNode> parentMap, EvaluationContext context)
			throws Exception {
		if (first)
			container.clear();
		int childCount = node.getChildCount();
		for (int a = 0; a < childCount; a++) {
			SpelNode child = node.getChild(a);
			parentMap.put(child, node);
			walkAst(child, false, initial, container, parentMap, context);
		}
		if ((org.springframework.expression.spel.ast.PropertyOrFieldReference.class)
				.isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.PropertyOrFieldReference tmp = (org.springframework.expression.spel.ast.PropertyOrFieldReference) node;
				final SpelNode parent = parentMap.get(tmp);
				if(parent != null) {
					if ((org.springframework.expression.spel.ast.MethodReference.class)
							.isAssignableFrom(parent.getClass())) {
						ElementVector newVector = new ElementVector();
						newVector.getTupels().put(tmp.toStringAST(), initial.getTupels().get(tmp.toStringAST() + ".value"));
						container.put(tmp.hashCode() + "", newVector);
					}
				}
		} else if ((org.springframework.expression.spel.ast.CompoundExpression.class)
				.isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.CompoundExpression tmp = (org.springframework.expression.spel.ast.CompoundExpression) node;
			ElementVector newVector = new ElementVector();
			boolean found = false;
			final int tmpChildCount = tmp.getChildCount();
			for (int a = 0; a < tmpChildCount; a++) {
				final SpelNode tmpChild = tmp.getChild(a);
				if (container.containsKey(tmpChild.hashCode() + "")) {
					newVector = container.get(tmpChild.hashCode() + "");
					found = true;
					break;
				}
			}
			if (!found) {
				Set<Element> variableTupel = initial.getTupels().get(tmp.toStringAST());
				if(tmp.toStringAST().contentEquals("sessionController.participant")) {
					variableTupel = new HashSet<Element>();
				}
				if(variableTupel == null) throw new ParseException(0,"no Tupel found for "+tmp.toStringAST());
				final Set<Element> vector = new LinkedHashSet<Element>();
				if (!parentMap.containsKey(node)) {
					for (Element tmp1 : variableTupel) {
						Object valueObj = tmp1.getValue();
						if (((Boolean.class).isAssignableFrom(valueObj.getClass())) && (!((Boolean) valueObj)))
							continue;
						vector.add(tmp1);
					}
				} else {
					for (Element tmp1 : variableTupel) {
						vector.add(tmp1);
					}
				}
				newVector.getTupels().put(tmp.toStringAST(), vector);
			}
			final SpelNode parent = parentMap.get(tmp);
			if (parent != null) {
				if ((org.springframework.expression.spel.ast.OpAnd.class).isAssignableFrom(parent.getClass())) {
					Map<String, Set<Element>> map = newVector.getTupels();
					if (map.containsKey(tmp.toStringAST())) {
						Set<Element> set = map.get(tmp.toStringAST());
						Set<Element> newSet = new LinkedHashSet<Element>();
						for (Element element : set) {
							if (element.getValue().equals(true))
								newSet.add(element);
						}
						map.put(tmp.toStringAST(), newSet);
					}
					newVector.setTupels(map);
				}
			}
			for (int a = 0; a < childCount; a++) {
				final SpelNode tmpChild = tmp.getChild(a);
				container.remove(tmpChild.hashCode() + "");
			}
			container.put(tmp.hashCode() + "", newVector);
		} else if ((org.springframework.expression.spel.ast.OpEQ.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.OpEQ tmp = (org.springframework.expression.spel.ast.OpEQ) node;
			final ElementVector leftVector = container.get(tmp.getLeftOperand().hashCode() + "");
			final ElementVector rightVector = container.get(tmp.getRightOperand().hashCode() + "");
			 final ElementVector newVector = new ElementVector();
			if ((leftVector != null)&&(rightVector != null)) {
				final Map<String, Set<Element>> leftTupels = leftVector.getTupels();
				final Map<String, Set<Element>> rightTupels = rightVector.getTupels();
				boolean found = false;
				for (final Map.Entry<String, Set<Element>> leftSet : leftTupels.entrySet()) {
					for (final Element leftElement : leftSet.getValue()) {
						for (final Map.Entry<String, Set<Element>> rightSet : rightTupels.entrySet()) {
							for (final Element rightElement : rightSet.getValue()) {
								if (leftElement.getValue().equals(rightElement.getValue())) {
									Set<Element> newSet = null;
									if (newVector.getTupels().containsKey(leftSet.getKey()))
										newSet = newVector.getTupels().get(leftSet.getKey());
									if (newSet == null)
										newSet = new LinkedHashSet<Element>();
									newSet.add(rightElement);
									newVector.getTupels().put(leftSet.getKey(), newSet);
									found = true;
								}
							}
						}
					}
				}
				found = true;
				if (!found)
					throw new Exception("No equal Value found " + leftVector + " != " + rightVector + "");
			}
			container.remove(tmp.getLeftOperand().hashCode() + "");
			container.remove(tmp.getRightOperand().hashCode() + "");
			container.put(node.hashCode() + "", newVector);
		} else if ((org.springframework.expression.spel.ast.OpNE.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.OpNE tmp = (org.springframework.expression.spel.ast.OpNE) node;
			final ElementVector leftVector = container.get(tmp.getLeftOperand().hashCode() + "");
			final ElementVector rightVector = container.get(tmp.getRightOperand().hashCode() + "");
			if ((leftVector != null) && (rightVector != null)) {
				boolean found = false;
				final ElementVector newVectors = new ElementVector();
				for (final Map.Entry<String, Set<Element>> leftTupel : leftVector.getTupels().entrySet()) {
					final String variable = leftTupel.getKey();
					final Set<Element> leftValues = leftTupel.getValue();
					for (final Map.Entry<String, Set<Element>> rightTupel : rightVector.getTupels().entrySet()) {
						final Set<Element> rightValues = rightTupel.getValue();
						if ((leftValues != null) && (rightValues != null)) {
							Set<Element> vector = new LinkedHashSet<Element>();
							for (final Element leftValue : leftValues) {
								for (final Element rightValue : rightValues) {
									if (!leftValue.getValue().equals(rightValue.getValue())) {
										vector.add(leftValue);
										found = true;
									}
								}
							}
							newVectors.getTupels().put(variable, vector);
						}
					}
				}
				found = true;
				if (!found)
					throw new Exception("No non-equal Value found (" + leftVector + ") != (" + rightVector + ")");
				container.remove(tmp.getLeftOperand().hashCode() + "");
				container.remove(tmp.getRightOperand().hashCode() + "");
				container.put(node.hashCode() + "", newVectors);
			}
		} else if ((org.springframework.expression.spel.ast.OpGE.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.OpGE tmp = (org.springframework.expression.spel.ast.OpGE) node;
			final ElementVector leftTermVector = container.get(tmp.getLeftOperand().hashCode() + "");
			final ElementVector rightTermVector = container.get(tmp.getRightOperand().hashCode() + "");
			if ((leftTermVector != null) && (rightTermVector != null)) {
				final ElementVector newVectors = new ElementVector();
				for (final Map.Entry<String, Set<Element>> leftTupel : leftTermVector.getTupels().entrySet()) {
					final String variable = leftTupel.getKey();
					final Set<Element> leftValues = leftTupel.getValue();
					for (final Map.Entry<String, Set<Element>> rightTupel : rightTermVector.getTupels().entrySet()) {
						final Set<Element> rightValues = rightTupel.getValue();
						if ((leftValues != null) && (rightValues != null)) {
							final Set<Element> vector = new LinkedHashSet<Element>();
							for (final Element leftValue : leftValues) {
								String leftValueStr = leftValue.getValue()+"";
								leftValueStr = leftValueStr.replaceAll(Pattern.quote("'"), "");
								for (final Element rightValue : rightValues) {
									String rightValueStr = rightValue.getValue()+"";
									rightValueStr = rightValueStr.replaceAll(Pattern.quote("'"), "");
									if (Double.parseDouble(leftValueStr) >= Double
											.parseDouble(rightValueStr)) {
										vector.add(leftValue);
									}
								}
							}
							newVectors.getTupels().put(variable, vector);
						}
					}
				}
				container.remove(tmp.getLeftOperand().hashCode() + "");
				container.remove(tmp.getRightOperand().hashCode() + "");
				container.put(node.hashCode() + "", newVectors);
			}
		} else if ((org.springframework.expression.spel.ast.OpGT.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.OpGT tmp = (org.springframework.expression.spel.ast.OpGT) node;
			final ElementVector leftTermVector = container.get(tmp.getLeftOperand().hashCode() + "");
			final ElementVector rightTermVector = container.get(tmp.getRightOperand().hashCode() + "");
			if ((leftTermVector != null) && (rightTermVector != null)) {
				final ElementVector newVectors = new ElementVector();
				for (final Map.Entry<String, Set<Element>> leftTupel : leftTermVector.getTupels().entrySet()) {
					final String variable = leftTupel.getKey();
					final Set<Element> leftValues = leftTupel.getValue();
					for (final Map.Entry<String, Set<Element>> rightTupel : rightTermVector.getTupels().entrySet()) {
						final Set<Element> rightValues = rightTupel.getValue();
						if ((leftValues != null) && (rightValues != null)) {
							final Set<Element> vector = new LinkedHashSet<Element>();
							for (final Element leftValue : leftValues) {
								String leftValueStr = leftValue.getValue()+"";
								leftValueStr = leftValueStr.replaceAll(Pattern.quote("'"), "");
								for (final Element rightValue : rightValues) {
									String rightValueStr = rightValue.getValue()+"";
									rightValueStr = rightValueStr.replaceAll(Pattern.quote("'"), "");
									if (Double.parseDouble(leftValueStr) > Double
											.parseDouble(rightValueStr)) {
										vector.add(leftValue);
									}
								}
							}
							newVectors.getTupels().put(variable, vector);
						}
					}
				}
				container.remove(tmp.getLeftOperand().hashCode() + "");
				container.remove(tmp.getRightOperand().hashCode() + "");
				container.put(node.hashCode() + "", newVectors);
			}
		} else if ((org.springframework.expression.spel.ast.OpLE.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.OpLE tmp = (org.springframework.expression.spel.ast.OpLE) node;
			final ElementVector leftTermVector = container.get(tmp.getLeftOperand().hashCode() + "");
			final ElementVector rightTermVector = container.get(tmp.getRightOperand().hashCode() + "");
			if ((leftTermVector != null) && (rightTermVector != null)) {
				final ElementVector newVectors = new ElementVector();
				for (final Map.Entry<String, Set<Element>> leftTupel : leftTermVector.getTupels().entrySet()) {
					final String variable = leftTupel.getKey();
					final Set<Element> leftValues = leftTupel.getValue();
					for (final Map.Entry<String, Set<Element>> rightTupel : rightTermVector.getTupels().entrySet()) {
						final Set<Element> rightValues = rightTupel.getValue();
						if ((leftValues != null) && (rightValues != null)) {
							final Set<Element> vector = new LinkedHashSet<Element>();
							for (final Element leftValue : leftValues) {
								String leftValueStr = leftValue.getValue()+"";
								leftValueStr = leftValueStr.replaceAll(Pattern.quote("'"), "");
								for (final Element rightValue : rightValues) {
									String rightValueStr = rightValue.getValue()+"";
									rightValueStr = rightValueStr.replaceAll(Pattern.quote("'"), "");
									if (Double.parseDouble(leftValueStr) <= Double
											.parseDouble(rightValue.getValue() + "")) {
										vector.add(leftValue);
									}
								}
							}
							newVectors.getTupels().put(variable, vector);
						}
					}
				}
				container.remove(tmp.getLeftOperand().hashCode() + "");
				container.remove(tmp.getRightOperand().hashCode() + "");
				container.put(node.hashCode() + "", newVectors);
			}
		} else if ((org.springframework.expression.spel.ast.OpLT.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.OpLT tmp = (org.springframework.expression.spel.ast.OpLT) node;
			final ElementVector leftTermVector = container.get(tmp.getLeftOperand().hashCode() + "");
			final ElementVector rightTermVector = container.get(tmp.getRightOperand().hashCode() + "");
			final ElementVector newVectors = new ElementVector();
			if ((leftTermVector != null) && (rightTermVector != null)) {
				for (final Map.Entry<String, Set<Element>> leftTupel : leftTermVector.getTupels().entrySet()) {
					final String variable = leftTupel.getKey();
					final Set<Element> leftValues = leftTupel.getValue();
					for (final Map.Entry<String, Set<Element>> rightTupel : rightTermVector.getTupels().entrySet()) {
						final Set<Element> rightValues = rightTupel.getValue();
						if ((leftValues != null) && (rightValues != null)) {
							final Set<Element> vector = new LinkedHashSet<Element>();
							for (final Element leftValue : leftValues) {
								String leftValueStr = leftValue.getValue()+"";
								leftValueStr = leftValueStr.replaceAll(Pattern.quote("'"), "");
								for (final Element rightValue : rightValues) {
									String rightValueStr = rightValue.getValue()+"";
									rightValueStr = rightValueStr.replaceAll(Pattern.quote("'"), "");
									if (Double.parseDouble(leftValueStr) < Double.parseDouble(rightValueStr)) {
										vector.add(leftValue);
									}
								}
							}
							newVectors.getTupels().put(variable, vector);
						}
					}
				}
				container.remove(tmp.getLeftOperand().hashCode() + "");
				container.remove(tmp.getRightOperand().hashCode() + "");
			}
			container.put(node.hashCode() + "", newVectors);
		} else if ((org.springframework.expression.spel.ast.OperatorNot.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.OperatorNot tmp = (org.springframework.expression.spel.ast.OperatorNot) node;
			final SpelNode term = tmp.getChild(0);
			final ElementVector newVectors = new ElementVector();
			final ElementVector termVector = container.get(term.hashCode() + "");
			final Map<String, Set<Element>> termTupel = termVector.getTupels();
			final Map<String, Set<Element>> initialTupel = initial.getTupels();
			final Set<Element> binarySet = new HashSet<Element>();
			binarySet.add(new Element(true));
			binarySet.add(new Element(false));
			for (final Map.Entry<String, Set<Element>> variableTupel : initialTupel.entrySet()) {
				final String variable = variableTupel.getKey();
				final Set<Element> initials = variableTupel.getValue();
				final Set<Element> possibles = termTupel.get(variable);
				if (possibles != null) {
					if (possibles.equals(binarySet))
						possibles.remove(new Element(false));
					final Set<Element> vector = new LinkedHashSet<Element>();
					for (final Element tmp1 : initials) {
						if (!possibles.contains(tmp1)) {
							vector.add(tmp1);
						}
					}
					newVectors.getTupels().put(variable, vector);
				}
			}
			container.remove(term.hashCode() + "");
			container.put(node.hashCode() + "", newVectors);
		} else if ((org.springframework.expression.spel.ast.StringLiteral.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.StringLiteral tmp = (org.springframework.expression.spel.ast.StringLiteral) node;
			final ElementVector newVector = new ElementVector();
			final Set<Element> vector = new LinkedHashSet<Element>();
			vector.add(new Element(tmp.toStringAST()));
			newVector.getTupels().put(tmp.toStringAST(), vector);
			container.put(tmp.hashCode() + "", newVector);
		} else if ((org.springframework.expression.spel.ast.BooleanLiteral.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.BooleanLiteral tmp = (org.springframework.expression.spel.ast.BooleanLiteral) node;
			final ElementVector newVector = new ElementVector();
			final Set<Element> vector = new LinkedHashSet<Element>();
			vector.add(new Element(tmp.toStringAST()));
			newVector.getTupels().put(tmp.toStringAST(), vector);
			container.put(tmp.hashCode() + "", newVector);
		} else if ((org.springframework.expression.spel.ast.IntLiteral.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.IntLiteral tmp = (org.springframework.expression.spel.ast.IntLiteral) node;
			final ElementVector newVector = new ElementVector();
			final Set<Element> vector = new LinkedHashSet<Element>();
			vector.add(new Element(tmp.toStringAST()));
			newVector.getTupels().put(tmp.toStringAST(), vector);
			container.put(tmp.hashCode() + "", newVector);
		} else if ((org.springframework.expression.spel.ast.OpOr.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.OpOr tmp = (org.springframework.expression.spel.ast.OpOr) node;
			final SpelNode leftTerm = tmp.getLeftOperand();
			final String leftKey = leftTerm.hashCode() + "";
			final SpelNode rightTerm = tmp.getRightOperand();
			final String rightKey = rightTerm.hashCode() + "";
			final ElementVector leftVector = container.get(leftKey);
			final ElementVector rightVector = container.get(rightKey);
			final ElementVector newVectors = new ElementVector();
			if (leftVector != null) {
				for (final Map.Entry<String, Set<Element>> variableTupel : leftVector.getTupels().entrySet()) {
					final String variable = variableTupel.getKey();
					final Set<Element> possibles = leftVector.getTupels().get(variable);
					if (possibles != null) {
						newVectors.getTupels().put(variable, possibles);
					}
				}
			}
			if (rightVector != null) {
				for (final Map.Entry<String, Set<Element>> variableTupel : rightVector.getTupels().entrySet()) {
					final String variable = variableTupel.getKey();
					final Set<Element> possibles = rightVector.getTupels().get(variable);
					final Set<Element> leftPossibles = newVectors.getTupels().get(variable);
					final Set<Element> newPossibles = new HashSet<Element>();
					if (leftPossibles != null)
						newPossibles.addAll(leftPossibles);
					if (possibles != null)
						newPossibles.addAll(possibles);
					newVectors.getTupels().put(variable, newPossibles);
				}
			}
			container.remove(leftTerm.hashCode() + "");
			container.remove(rightTerm.hashCode() + "");
			container.put(node.hashCode() + "", newVectors);
		} else if ((org.springframework.expression.spel.ast.OpAnd.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.OpAnd tmp = (org.springframework.expression.spel.ast.OpAnd) node;
			final SpelNode leftTerm = tmp.getLeftOperand();
			final String leftKey = leftTerm.hashCode() + "";
			final SpelNode rightTerm = tmp.getRightOperand();
			final String rightKey = rightTerm.hashCode() + "";
			final ElementVector leftVector = container.get(leftKey);
			final ElementVector rightVector = container.get(rightKey);
			final ElementVector newVectors = new ElementVector();
			if((leftVector != null)&&(rightVector != null)) {
				for (final Map.Entry<String, Set<Element>> variableTupel : leftVector.getTupels().entrySet()) {
					final String variable = variableTupel.getKey();
					final Set<Element> possibles = leftVector.getTupels().get(variable);
					if (possibles != null) {
						Set<Element> otherPossibles = rightVector.getTupels().get(variable);
						if (otherPossibles == null)
							otherPossibles = initial.getTupels().get(variable);
						if (otherPossibles != null) {
							final Set<Element> vector = new LinkedHashSet<Element>();
							for (final Element possible : possibles) {
								vector.add(possible);
							}
							newVectors.getTupels().put(variable, vector);
						}
					}
				}
				for (final Map.Entry<String, Set<Element>> variableTupel : rightVector.getTupels().entrySet()) {
					final String variable = variableTupel.getKey();
					final Set<Element> possibles = rightVector.getTupels().get(variable);
					if (possibles != null) {
						Set<Element> otherPossibles = leftVector.getTupels().get(variable);
						if ((otherPossibles == null))
							otherPossibles = initial.getTupels().get(variable);
						if (otherPossibles != null) {
							final Set<Element> vector = new LinkedHashSet<Element>();
							for (Element possible : possibles) {
								vector.add(possible);
							}
							newVectors.getTupels().put(variable, vector);
						}
					}
				}
			}
			container.remove(leftTerm.hashCode() + "");
			container.remove(rightTerm.hashCode() + "");
			container.put(node.hashCode() + "", newVectors);
		} else if ((org.springframework.expression.spel.ast.MethodReference.class).isAssignableFrom(node.getClass())) {
			org.springframework.expression.spel.ast.MethodReference tmp = (org.springframework.expression.spel.ast.MethodReference) node;
			final String method = tmp.toStringAST();
			final ElementVector newVectors = new ElementVector();
				final int methodChildCount = tmp.getChildCount();
				for (int k = 0; k < methodChildCount; k++) {
					final SpelNode methodChildNode = tmp.getChild(k);
					final ElementVector childVector = container.get(methodChildNode.hashCode() + "");
					if(childVector != null) {
					if (childVector.getTupels().containsKey(methodChildNode.toStringAST()))
						childVector.getTupels().remove(methodChildNode.toStringAST());
					childVector.getTupels().put(methodChildNode.toStringAST() + ".value", new LinkedHashSet<Element>());
					newVectors.getTupels().putAll(childVector.getTupels());
					}
					container.remove(methodChildNode.hashCode() + "");
				}
			container.put(node.hashCode() + "", newVectors);
		} else {
		}
	}
}
