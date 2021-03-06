/* Generated by Streams Studio: February 27, 2014 5:32:09 PM EST */
package com.acme.test;


import org.apache.log4j.Logger;

import com.ibm.streams.operator.AbstractOperator;
import com.ibm.streams.operator.OperatorContext;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.StreamingData.Punctuation;
import com.ibm.streams.operator.StreamingInput;
import com.ibm.streams.operator.StreamingOutput;
import com.ibm.streams.operator.Tuple;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.InputPortSet.WindowMode;
import com.ibm.streams.operator.model.InputPortSet.WindowPunctuationInputMode;
import com.ibm.streams.operator.model.InputPorts;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.OutputPortSet.WindowPunctuationOutputMode;
import com.ibm.streams.operator.model.OutputPorts;
import com.ibm.streams.operator.model.PrimitiveOperator;

/**
 * Class for an operator that receives a tuple and then optionally submits a tuple. 
 * This pattern supports one or more input streams and one or more output streams. 
 * <P>
 * The following event methods from the Operator interface can be called:
 * </p>
 * <ul>
 * <li><code>initialize()</code> to perform operator initialization</li>
 * <li>allPortsReady() notification indicates the operator's ports are ready to process and submit tuples</li> 
 * <li>process() handles a tuple arriving on an input port 
 * <li>processPuncuation() handles a punctuation mark arriving on an input port 
 * <li>shutdown() to shutdown the operator. A shutdown request may occur at any time, 
 * such as a request to stop a PE or cancel a job. 
 * Thus the shutdown() may occur while the operator is processing tuples, punctuation marks, 
 * or even during port ready notification.</li>
 * </ul>
 * <p>With the exception of operator initialization, all the other events may occur concurrently with each other, 
 * which lead to these methods being called concurrently by different threads.</p> 
 */
@PrimitiveOperator(name="MyJavaOp", namespace="com.acme.test",
description="Java Operator MyJavaOp")
@InputPorts({@InputPortSet(description="Port that ingests tuples", cardinality=1, optional=false, windowingMode=WindowMode.NonWindowed, windowPunctuationInputMode=WindowPunctuationInputMode.Oblivious), @InputPortSet(description="Optional input ports", optional=true, windowingMode=WindowMode.NonWindowed, windowPunctuationInputMode=WindowPunctuationInputMode.Oblivious)})
@OutputPorts({@OutputPortSet(description="Port that produces tuples", cardinality=1, optional=false, windowPunctuationOutputMode=WindowPunctuationOutputMode.Generating), @OutputPortSet(description="Optional output ports", optional=true, windowPunctuationOutputMode=WindowPunctuationOutputMode.Generating)})
public class MyJavaOp extends AbstractOperator {
	
	// Declare the thread handles here.
	// We will independently process the incoming tuples in these 
	// threads rather than in the standard process method.
	WorkerThread thread1 = null;
	WorkerThread thread2 = null;
	WorkerThread thread3 = null;
	WorkerThread thread4 = null;
	WorkerThread thread5 = null;
	
    /**
     * Initialize this operator. Called once before any tuples are processed.
     * @param context OperatorContext for this operator.
     * @throws Exception Operator failure, will cause the enclosing PE to terminate.
     */
	@Override
	public synchronized void initialize(OperatorContext context)
			throws Exception {
    	// Must call super.initialize(context) to correctly setup an operator.
		super.initialize(context);
        Logger.getLogger(this.getClass()).trace("Operator " + context.getName() + " initializing in PE: " + context.getPE().getPEId() + " in Job: " + context.getPE().getJobId() );
        
        // TODO:
        // If needed, insert code to establish connections or resources to communicate an external system or data store.
        // The configuration information for this may come from parameters supplied to the operator invocation, 
        // or external configuration files or a combination of the two.
        
        // Create many threads to handle the incoming tuples.
        thread1 = new WorkerThread();
        thread1.start();
        
        thread2 = new WorkerThread();
        thread2.start();
        
        thread3 = new WorkerThread();
        thread3.start();
        
        thread4 = new WorkerThread();
        thread4.start();
        
        thread5 = new WorkerThread();
        thread5.start();
        
	}

    /**
     * Notification that initialization is complete and all input and output ports 
     * are connected and ready to receive and submit tuples.
     * @throws Exception Operator failure, will cause the enclosing PE to terminate.
     */
    @Override
    public synchronized void allPortsReady() throws Exception {
    	// This method is commonly used by source operators. 
    	// Operators that process incoming tuples generally do not need this notification. 
        OperatorContext context = getOperatorContext();
        Logger.getLogger(this.getClass()).trace("Operator " + context.getName() + " all ports are ready in PE: " + context.getPE().getPEId() + " in Job: " + context.getPE().getJobId() );
    }

    /**
     * Process an incoming tuple that arrived on the specified port.
     * <P>
     * Copy the incoming tuple to a new output tuple and submit to the output port. 
     * </P>
     * @param inputStream Port the tuple is arriving on.
     * @param tuple Object representing the incoming tuple.
     * @throws Exception Operator failure, will cause the enclosing PE to terminate.
     */
    @Override
    public final void process(StreamingInput<Tuple> inputStream, Tuple tuple)
            throws Exception {

    	/*
    	// Create a new tuple for output port 0
        StreamingOutput<OutputTuple> outStream = getOutput(0);
        OutputTuple outTuple = outStream.newTuple();

        // Copy across all matching attributes.
        outTuple.assign(tuple);

        // TODO: Insert code to perform transformation on output tuple as needed:
        // outTuple.setString("AttributeName", "AttributeValue");

        // Submit new tuple to output port 0
        outStream.submit(outTuple);
        */
    	
    	
    	// Instead of processing the incoming tuple in this standard process method, we are going to
    	// signal 5 different threads to process the incoming tuple and send their
    	// own output tuples from those individual threads.
    	//
    	// The signaling mechanism used here is just for the purpose of demonstration in this example.
    	// You may want to come up with your own sophisticated way to wake up the
    	// threads to process the newly arrived incoming tuples.
    	thread1.setInputTuple(tuple);
    	thread2.setInputTuple(tuple);
    	thread3.setInputTuple(tuple);
    	thread4.setInputTuple(tuple);
    	thread5.setInputTuple(tuple);
    }
    
    /**
     * Process an incoming punctuation that arrived on the specified port.
     * @param stream Port the punctuation is arriving on.
     * @param mark The punctuation mark
     * @throws Exception Operator failure, will cause the enclosing PE to terminate.
     */
    @Override
    public void processPunctuation(StreamingInput<Tuple> stream,
    		Punctuation mark) throws Exception {
    	// For window markers, punctuate all output ports 
    	super.processPunctuation(stream, mark);
    }

    /**
     * Shutdown this operator.
     * @throws Exception Operator failure, will cause the enclosing PE to terminate.
     */
    public synchronized void shutdown() throws Exception {
        OperatorContext context = getOperatorContext();
        Logger.getLogger(this.getClass()).trace("Operator " + context.getName() + " shutting down in PE: " + context.getPE().getPEId() + " in Job: " + context.getPE().getJobId() );
        
        // TODO: If needed, close connections or release resources related to any external system or data store.
        // Make all our worker threads to drop out of their infinite processing loop.
        thread1.resetInfiniteLooping();
        thread2.resetInfiniteLooping();
        thread3.resetInfiniteLooping();
        thread4.resetInfiniteLooping();
        thread5.resetInfiniteLooping();
        
        // Must call super.shutdown()
        super.shutdown();
    }

    // We can process the input tuple from any number of threads at 
    // our own pace and submit the output tuple arbitrarily at any time.
    // That is exactly what we demonstrate in the thread below.
    public class WorkerThread extends Thread {
    	// A copy of the newly arrived tuple will be available here.
    	private Tuple newIncomingTuple;
    	// This flag will tell us whenever there is a new input tuple ready for processing.
    	private boolean newIncomingTupleArrived = false;
    	// This thread will loop forever when this flag is set to true.
    	private boolean loopForEver = true;
    	
    	@Override
    	public void run() {
    		// We will loop forever in this thread until this operator is shutdown.
    		// During the operator shutdown callback, loopForEver flag will be reset to
    		// false so that we can do a graceful exit from this spinning thread.
    		while(loopForEver) {
    			try {
    				// Sleep for 50 milliseconds. This is just an example.
    				// You can do something else instead of sleeping.
    				Thread.sleep(50);
    			} catch (Exception ex) {
    				;
    			}
    			
    			// The following logic is just an example. 
    			// You have to design your own business logic in this thread to suit your needs.
    			if (newIncomingTupleArrived) {
    				newIncomingTupleArrived = false;
    				// Our tuple processing logic is very simple here.
    				// We will simply increment the value by 1 in all the incoming tuple attributes.
    		    	// Create a new tuple for output port 0
    		        StreamingOutput<OutputTuple> outStream = getOutput(0);
    		        OutputTuple outTuple = outStream.newTuple();

    		        // Copy across all matching attributes.
    		        outTuple.assign(newIncomingTuple);
    		        // Increment the tuple attribute value by 1.
    		        outTuple.setInt("i", newIncomingTuple.getInt("i") + 1);
    		        // Increment this tuple attribute value by 1.7
    		        outTuple.setDouble("j", newIncomingTuple.getDouble("j") + 1.7);
    		        // Assign the thread id to this attribute.
    		        outTuple.setString("threadId", "Thread " + Long.toString(Thread.currentThread().getId()));
    		       
    		        try {
    		        	// Submit new tuple to output port 0
    		        	outStream.submit(outTuple);
    		        } catch(Exception ex) {
    		        	// Do something with this exception if you want to.
    		        }
    			}
    		}
    	}
    	
    	public void setInputTuple(Tuple tuple) {
    		// Copy the input tuple for this thread to process.
    		newIncomingTuple = tuple;
    		// Set this flag so that this thread will know there is a
    		// new input tuple ready for processing.
    		newIncomingTupleArrived = true;
    	}
    	
    	public void resetInfiniteLooping() {
    		// This operator is being shutdown.
    		// Reset this flag so that the thread can exit gracefully.
    		loopForEver = false;
    	}
    	
    }
}
