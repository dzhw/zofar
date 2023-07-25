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
/*
 * Class to handle decisions
 */
package eu.dzhw.zofar.management.utils.bool;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
/**
 * The Class DecisionClient.
 */
public class DecisionClient {
	/** The Constant INSTANCE. */
	private static final DecisionClient INSTANCE = new DecisionClient();
	/** The Constant LOGGER. */
	final static Logger LOGGER = LoggerFactory.getLogger(DecisionClient.class);
	/** The random. */
	private final Random random;
	/**
	 * Instantiates a new decision client.
	 */
	private DecisionClient() {
		super();
		this.random = new Random();
	}
	/**
	 * Gets the single instance of DecisionClient.
	 * 
	 * @return single instance of DecisionClient
	 */
	public static synchronized DecisionClient getInstance() {
		return INSTANCE;
	}
	/**
	 * Generate a random Boolean Decision with an probability of 50%.
	 * 
	 * @return random boolean
	 */
	public boolean randomBoolean() {
		return this.random.nextBoolean();
	}
	/**
	 * Generate a weighted random Boolean Decision.
	 * 
	 * @param percent
	 *            Value between 1 and 100
	 * @return random boolean
	 */
	public boolean randomBoolean(final int percent) {
		final double weight = ((double) percent) / 100;
		final double rnd = this.random.nextDouble();
		final boolean back = rnd < weight;
		return back;
	}
	public boolean evaluateSpel(final String condition, final Map<String,Object> data) throws Exception{
		return this.evaluateSpel(condition, data,null);
	}
	public boolean evaluateSpel(final String condition, final Map<String,Object> data,final String additional) throws Exception{
		try{
			final StandardEvaluationContext context = new StandardEvaluationContext();
			final PropertyAccessor accessor = new PropertyAccessor() {
				public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
					return true;
				}
				public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
					boolean debug = false;
					if(name.contains("h_gartcount")) {
						debug = true;
					}
					if (target != null) {
						try {
							Field field = target.getClass().getDeclaredField(name);
							if(debug)LOGGER.info("Field of "+name+" : "+field);
							if(field == null) return null;
							field.setAccessible(true);
							final Object value = field.get(target);
							if(debug)LOGGER.info("value ({}) {}",target+", "+value.getClass().getSimpleName(),name+"="+value);
							final TypedValue back =  new TypedValue(value);
							if(debug)LOGGER.info("back : "+back);
							return back;
						} catch (Exception e) {
							LOGGER.error("("+additional+") ["+condition+"] Target: "+target+"  Name: "+name+" Error while evaluateSpel", e);
						}
						return null;
					} else{
						Object value = data.get(name);
						if(value == null) {
							value = false;
						}
							final TypedValue back =  new TypedValue(value);
							return back;
					}
				}
				public Class<?>[] getSpecificTargetClasses() {
					return null;
				}
				public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
					return true;
				}
				public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
				}
			};
			context.addPropertyAccessor(accessor);
			ExpressionParser parser = new SpelExpressionParser();
			Expression exp = parser.parseExpression(condition);
			if(exp == null)return false;
			Object result = exp.getValue(context);
			if(result == null)return false;
			Boolean message = null;
			if(result != null)message = (Boolean)result;
			if(message != null)return message;
		}
		catch(Exception e){
			if((java.lang.NoSuchFieldException.class).isAssignableFrom(e.getClass())) {
				LOGGER.error("NoSuchField",e);
			}
			else throw e;
		}
		return false;
	}
}
