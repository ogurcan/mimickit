package mimickit.model.neuron;


public interface SpikeObserver {
	 public abstract void update(Neuron spikingNeuron, double spikeTime);
}
