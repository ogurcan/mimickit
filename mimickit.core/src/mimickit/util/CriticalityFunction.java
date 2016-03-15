package mimickit.util;

public class CriticalityFunction {
	private float vMin;
	private float vMax;
	private float cMin;
	private float cMax;
	private float epsilon;
	
	public CriticalityFunction(float vMin, float vMax, float cMin, float cMax, float eps){
		this.vMin=vMin;
		this.vMax=vMax;
		this.cMin=cMin;
		this.cMax=cMax;
		epsilon = eps-vMin;
	}
	
	public float compute(float value){
		float eta = epsilon/3;
		float gamma = -2*cMax/epsilon;
		float dlta = -gamma*(epsilon-eta)/2;
		float x = value-vMin;				
		
		if(x<=0)			
			return cMax;
		
		if(x<=eta)				
			return (gamma*(x-eta)*(x-eta)/(2*eta)) + gamma*(x-eta) + dlta;	
		
		if(x<=epsilon)
			return -gamma*(x-eta)*(x-eta)/(2*(epsilon-eta)) + gamma*(x-eta) + dlta;
		
		if(x<=(vMax-vMin)-epsilon)
			return cMin;
		
		if(x<=(vMax-vMin)-eta)
			return -gamma*((vMax-vMin)-x-eta)*((vMax-vMin)-x-eta)/(2*(epsilon-eta)) + gamma*((vMax-vMin)-x-eta) + dlta;
		
		if(x<=(vMax-vMin))
			return gamma*((vMax-vMin)-x-eta)*((vMax-vMin)-x-eta)/(2*eta) + gamma*((vMax-vMin)-x-eta) + dlta;
		
		if(x>=(vMax-vMin))
			return cMax;		

		return cMax;
	}
}
