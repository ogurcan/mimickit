package mimickit.amas;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import mimickit.model.SOEASYParameters;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ScheduleParameters;

/**
 * @author Önder Gürcan
 * @version $Revision$ $Date$
 * 
 */
abstract public class CooperativeAgent {

	/**
	 * The agent who created this agent. If this agent is not created by another
	 * agent, this parameter is set to "null".
	 */
	private CooperativeAgent creatorAgent;

	/**
	 * The time of creation of this agent in terms of simulation clock (ticks).
	 */
	private double creationTime;

	/**
	 * 
	 */
	private List<ActionToBeScheduled> actionsToBeScheduled;

	/**
	 * Keeps track of the current incoming feedbacks received by this agent.
	 * This is cleared everytime the feedbacks in it are processed.
	 */
	private HashMap<ReceivedFeedback, NonCooperativeSituation> receivedFeedbackHashMap;

	/**
	 * Keeps the latest feedbacks sent to other agents.
	 */
	private HashMap<CooperativeAgent, Feedback> lastestOutgoingFeedbacks;

	/**
	 * Keeps the feedback type this agent can handle, and the corresponding
	 * handler actions.
	 */
	private HashMap<Class<? extends Feedback>, Class<? extends Action>> feedbackActionTable;

	/**
	 * Keeps track of the detected non-cooperative situations received as
	 * feedbacks and their numbers.
	 */
	private HashMap<CooperativeAgent, Hashtable<String, Double>> annoyanceTable;

	private List<ISchedulableAction> scheduledRepeatingActions;

	/**
	 * The creation cause of this agent.
	 */
	private String cause;

	private double coopateActionScheduleTime = -1;

	// EXPERIMENTAL STUDY -->

	/**
	 * Keeps track of NCSs this agent faced to and the criticality of these NCSs
	 * from this agent's perspective.
	 */
	private Vector<NonCooperativeSituation> ncsTable;

	private double kriticality = 0.0;

	// <-- EXPERIMENTAL STUDY

	/**
	 * 
	 * @param creatorAgent
	 *            the agent that creates this agent.
	 * @param cause
	 *            the cause of the creation of this agent.
	 */
	public CooperativeAgent(CooperativeAgent creatorAgent, String cause) {
		// set parameters
		this.creatorAgent = creatorAgent;
		this.cause = cause;

		// initialize properties
		this.actionsToBeScheduled = new ArrayList<ActionToBeScheduled>();
		this.receivedFeedbackHashMap = new HashMap<ReceivedFeedback, NonCooperativeSituation>();
		this.feedbackActionTable = new HashMap<Class<? extends Feedback>, Class<? extends Action>>();
		this.lastestOutgoingFeedbacks = new HashMap<CooperativeAgent, Feedback>();
		this.annoyanceTable = new HashMap<CooperativeAgent, Hashtable<String, Double>>();
		this.ncsTable = new Vector<NonCooperativeSituation>();
		this.creationTime = SOEASYParameters.getInstance().getCurrentTick();

		// schedule actions
		SOEASYParameters.schedule(ScheduleParameters.createOneTime(
				this.creationTime + 1, ScheduleParameters.FIRST_PRIORITY, 0),
				new IAction() {
					@Override
					public void execute() {
						scheduleActions();
					}
				});
	}

	/**
	 * Defines the actions for this agent.
	 */
	abstract protected void defineActions();

	/**
	 * Computes the current criticality of this agent. The computed criticality
	 * value should be between 0.0 and 100.0.
	 * 
	 * @return
	 */
	//abstract public double getCriticality();

	/**
	 * Returns the neighbour agents of this agent. This method need not to
	 * return all the neighbours of this agent, rather it return critically
	 * important ones.
	 * 
	 * @return
	 */
	abstract protected List<CooperativeAgent> getNeighbourAgents();

	/**
	 * Returns the agents that provides input to this agent.
	 * 
	 * @return
	 */
	abstract protected List<? extends CooperativeAgent> getInputAgents();

	/**
	 * 
	 * @param start
	 * @param priority
	 * @param action
	 */
	public void scheduleOneTimeAction(double start, double priority,
			IAction action) {
		ActionToBeScheduled actionToBeScheduled = new ActionToBeScheduled(
				start, 0, priority, 0, action);
		actionsToBeScheduled.add(actionToBeScheduled);
	}

	/**
	 * 
	 * @param start
	 * @param interval
	 * @param priority
	 * @param action
	 */
	public void scheduleRepeatingAction(double start, double interval,
			double priority, IAction action) {
		ActionToBeScheduled actionToBeScheduled = new ActionToBeScheduled(
				start, interval, priority, 0, action);
		actionsToBeScheduled.add(actionToBeScheduled);
	}

	protected List<ActionToBeScheduled> getActionToBeScheduled() {
		return this.actionsToBeScheduled;
	}

	/**
	 * Used to schedule actions in the initialization of this agent.
	 */
	final protected void scheduleActions() {
		defineActions();
		//
		for (int acIndex = 0; acIndex < getActionToBeScheduled().size(); acIndex++) {
			ActionToBeScheduled action = getActionToBeScheduled().get(acIndex);
			if (action.getInterval() > 0) {
				// schedule repeating action
				ISchedulableAction schedulableAction = SOEASYParameters
						.schedule(ScheduleParameters.createRepeating(
								action.getStart(), action.getInterval(),
								action.getPriority()), action.getAction());
				getScheduledRepeatingActions().add(schedulableAction);
			} else {
				// schedule one time action
				SOEASYParameters.schedule(ScheduleParameters.createOneTime(
						action.getStart(), action.getPriority(),
						action.getDuration()), action.getAction());
			}
		}
		getActionToBeScheduled().clear();
	}

	protected List<ISchedulableAction> getScheduledRepeatingActions() {
		if (scheduledRepeatingActions == null) {
			scheduledRepeatingActions = new ArrayList<ISchedulableAction>();
		}
		return scheduledRepeatingActions;
	}

	/**
	 * Used to receive feedbacks from other agents. Incoming feedbacks are
	 * stored in a Vector. Returns true if this agent receives the given
	 * feedback.
	 * 
	 * Since multi-threaded code can have different behaviors for different
	 * thread schedules, this method returns true even this agent received the
	 * feedback before.
	 */
	public boolean receiveFeedback(Feedback feedback,
			CooperativeAgent senderAgent) {
		boolean isReceived = false;

		if (!(feedback.getSource().equals(this) || feedback
				.isSentPreviouslyBy(this))) {

			ReceivedFeedback receivedFeedback = new ReceivedFeedback(feedback,
					isBackwardPropagated(senderAgent));

			if (!(this.receivedFeedbackHashMap.containsKey(receivedFeedback))) {

				// detect the NCS related to this feedback
				NonCooperativeSituation ncs = detectNCS(receivedFeedback);

				// update latest received feedbacks table with the associated
				// NCS
				this.receivedFeedbackHashMap.put(receivedFeedback, ncs);

				// schedule cooperate() action for suppressing the detected NCSs
				scheduleCooperateAction();
			}
			isReceived = true;
		}
		return isReceived;
	}

	private void scheduleCooperateAction() {
		double currentTick = SOEASYParameters.getInstance().getCurrentTick();
		if (coopateActionScheduleTime < currentTick + 1) {
			SOEASYParameters.schedule(
					ScheduleParameters.createOneTime(currentTick + 1, 1, 0),
					new IAction() {
						@Override
						public void execute() {
							cooperate();
						}
					});
			coopateActionScheduleTime = currentTick + 1;
		}
	}

	private NonCooperativeSituation detectNCS(ReceivedFeedback receivedFeedback) {
		NonCooperativeSituation result = null;

		CooperativeAgent source = receivedFeedback.getFeedback().getSource();
		Feedback feedback = receivedFeedback.getFeedback();

		boolean isUpdated = false;
		Iterator<NonCooperativeSituation> iterator = ncsTable.iterator();
		while (iterator.hasNext()) {
			NonCooperativeSituation nextNCS = iterator.next();
			if (nextNCS.isItThisProblem(source, feedback)) {
				nextNCS.update(feedback);
				result = nextNCS;
				isUpdated = true;
				break;
			}
		}

		if (!isUpdated) {
			result = new NonCooperativeSituation(source, feedback);
			ncsTable.add(result);
		}

		// the criticality is set to the last detected NCS's annoyance level
		this.kriticality = result.getAnnoyance();

		return result;
	}

	abstract protected boolean isBackwardPropagated(CooperativeAgent senderAgent);

	/**
	 * Tries to cooperate with other agents.
	 */
	protected void cooperate() {
		// if (isMostCritical()) {
		// } else {
		handleFeedbacksNEW();
		// }
	}

	final protected void handleFeedbacksNEW() {
		double annoyance = 0.0;
		ReceivedFeedback chosenReceivedFeedback = null;
		NonCooperativeSituation chosenNCS = null;

		Iterator<ReceivedFeedback> iterator = receivedFeedbackHashMap.keySet()
				.iterator();
		while (iterator.hasNext()) {
			ReceivedFeedback nextReceivedFeedback = iterator.next();
			Feedback feedback = nextReceivedFeedback.getFeedback();
			CooperativeAgent source = feedback.getSource();

			//if (!feedback.isGood()) {
				NonCooperativeSituation nextNCS = receivedFeedbackHashMap
						.get(nextReceivedFeedback);
				//if (nextNCS.isItThisProblem(source, feedback)) {
					if (nextNCS.getAnnoyance() > annoyance) {
						annoyance = nextNCS.getAnnoyance();
						chosenReceivedFeedback = nextReceivedFeedback;
						chosenNCS = nextNCS;
					}
				//}
			//}
		}

		if (chosenReceivedFeedback != null) {
			// check if it is the biggest problem for this agent
			boolean isMostCriticalNCS = true;
//			Iterator<NonCooperativeSituation> ncsIterator = ncsTable.iterator();
//			while (ncsIterator.hasNext()) {
//				NonCooperativeSituation ncs = ncsIterator.next();
//				if (ncs.getAnnoyance() > chosenNCS.getAnnoyance()) {
//					isMostCriticalNCS = false;
//					break;
//				}
//			}

			// if decided to help...
			if (isMostCriticalNCS) {
				// find the most cooperative action and execute it
				Action action = decideTheMostCooperativeAction(chosenReceivedFeedback);
				if (action != null) {
					double start = SOEASYParameters.getInstance()
							.getCurrentTick() + 1;
					SOEASYParameters.schedule(
							ScheduleParameters.createOneTime(start, 0, 0),
							action);
				}
			}
		}

		// clear all the feedbacks
		receivedFeedbackHashMap.clear();
	}

	/**
	 * Decides what is the most cooperative action this agent has to undertake.
	 * 
	 * @param receivedFeedback
	 * @return
	 */
	private Action decideTheMostCooperativeAction(
			ReceivedFeedback receivedFeedback) {
		Action action = null;
		Class<? extends Feedback> feedbackClass = receivedFeedback
				.getFeedback().getClass();
		Class<? extends Action> actionClass = feedbackActionTable
				.get(feedbackClass);
		if (actionClass != null) {
			try {
				// get constructor public Action(Agent ownerAgent);
				Constructor<? extends Action> constructor = actionClass
						.getConstructor(CooperativeAgent.class);
				// Action action = new Action(this);
				action = constructor.newInstance(this);
				//
				action.setFeedback(receivedFeedback);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return action;
	}

	protected void defineFeedbackActionPair(
			Class<? extends Feedback> feedbackClass,
			Class<? extends Action> actionClass) {
		feedbackActionTable.put(feedbackClass, actionClass);
	}

	/**
	 * Used by actions of this agent to send feedbacks to other agents. Returns
	 * true if at least one agent receives the given feedback.
	 * 
	 * @param receiverAgents
	 *            the agents that will receive the feedbacks
	 * @param feedbackType
	 *            the type of the feedback to be sent
	 */
	public boolean sendFeedback(
			List<? extends CooperativeAgent> receiverAgents, Feedback feedback) {
		boolean isSentToAtLeastOneAgent = false;
		for (int index = 0; index < receiverAgents.size(); index++) {
			boolean feedbackSent = sendFeedback(receiverAgents.get(index),
					feedback);
			isSentToAtLeastOneAgent = isSentToAtLeastOneAgent || feedbackSent;
		}
		return isSentToAtLeastOneAgent;
	}

	/**
	 * Used by actions of this agent to send feedbacks to other agents. It does
	 * not send GOOD feedback if the latest feedback was also GOOD.
	 * 
	 * @param agents
	 *            the agents that will receive the feedbacks
	 * @param feedbackType
	 *            the type of the feedback to be sent
	 */
	public boolean sendFeedback(CooperativeAgent receiverAgent,
			Feedback feedback) {
		boolean isSent = false;
		if (receiverAgent != null) {
			Feedback lastFeedback = lastestOutgoingFeedbacks.get(receiverAgent);
			if (lastFeedback != null) {
				if (!(lastFeedback.isGood() && feedback.isGood())) {
					isSent = receiverAgent.receiveFeedback(feedback, this);
					if (isSent) {
						lastestOutgoingFeedbacks.put(receiverAgent, feedback);
						feedback.addSender(this); // sentFeedbacksHistory.add(feedback);
					}
				}
			} else {
				isSent = receiverAgent.receiveFeedback(feedback, this);
				if (isSent) {
					lastestOutgoingFeedbacks.put(receiverAgent, feedback);
					feedback.addSender(this);
				}
			}
		}
		return isSent;
	}

	/**
	 * Returns the annoyance level of this feedback. If the level is a positive
	 * integer, then the annoyance is about "increasing". If the level is a
	 * negative integer, then the annoyance is about "decreasing". Otherwise (if
	 * this agent is not annoyed from this feedback), the level is zero.
	 * 
	 * @param feedback
	 * @return
	 */
	public double getAnnoyanceLevel(Feedback feedback) {
		Double annoyanceLevel = 0.0;
		CooperativeAgent sourceAgent = feedback.getSource();
		Hashtable<String, Double> problemTable = annoyanceTable
				.get(sourceAgent);
		// if there is no problem about sourceAgent before
		if (problemTable == null) {
			annoyanceLevel = 0.0;
		} else {
			annoyanceLevel = problemTable.get(feedback.toString());
			if (annoyanceLevel == null) {
				annoyanceLevel = 0.0;
			}
		}
		return Math.abs(annoyanceLevel);
	}

	/**
	 * GOOD feedbacks cannot annoy an agent!
	 * 
	 * @param feedback
	 */
	public void annoy(Feedback feedback) {
		if (!feedback.isGood()) {
			CooperativeAgent sourceAgent = feedback.getSource();
			Hashtable<String, Double> problemTable = annoyanceTable
					.get(sourceAgent);
			// if there is no problem about sourceAgent before
			if (problemTable == null) {
				// annoy by 1
				problemTable = new Hashtable<String, Double>();
				if (feedback.isIncrease()) {
					problemTable.put(feedback.toString(), 1.0);
				} else {
					problemTable.put(feedback.toString(), -1.0);
				}
				annoyanceTable.put(sourceAgent, problemTable);
			} else {
				Double annoyanceLevel = problemTable.get(feedback.toString());
				if (annoyanceLevel == null) {
					if (feedback.isIncrease()) {
						problemTable.put(feedback.toString(), 1.0);
					} else {
						problemTable.put(feedback.toString(), -1.0);
					}
				} else {
					if (feedback.isIncrease()) {
						annoyanceLevel++;
						problemTable.put(feedback.toString(), annoyanceLevel);
					} else {
						annoyanceLevel--;
						problemTable.put(feedback.toString(), annoyanceLevel);
					}
				}
			}
		}
	}

	/**
	 * Resets annoyance about a feedback to zero.
	 * 
	 * @param feedback
	 */
	public void resetAnnoyance(Feedback feedback) {
		feedback.handle();
		CooperativeAgent sourceAgent = feedback.getSource();
		Hashtable<String, Double> problemTable = annoyanceTable
				.get(sourceAgent);

		if (problemTable != null) {
			problemTable.put(feedback.toString(), 0.0);
		}
	}

	public CooperativeAgent getCreatorAgent() {
		return creatorAgent;
	}

	public String getCause() {
		return cause;
	}

	public double getCreationTime() {
		return creationTime;
	}

	public double getKriticality() {
		// double kriticality = 0;
		// String problem = "";
		// //
		// Iterator<NonCooperativeSituation> iterator =
		// this.ncsTable.iterator();
		// while (iterator.hasNext()) {
		// NonCooperativeSituation ncs = iterator.next();
		// double annoyance = ncs.getAnnoyance();
		// if (annoyance > kriticality) {
		// kriticality = annoyance;
		// problem = ncs.getProblem();
		// }
		// }
		// if (kriticality > 1.0) {
		// if (this.toString().equals("InterNeuron0")) {
		// System.out.println(this + " is annoyed by " + problem + " by "
		// + kriticality);
		// }
		// }
		//
		return this.kriticality;
	}
	
	public double getCriticality() {
		return 0;
	}

}
