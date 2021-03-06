//
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy
// of the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an
// "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// The Original Code is State Machine Compiler(SMC).
// 
// The Initial Developer of the Original Code is Charles W. Rapp.
// Portions created by Charles W. Rapp are
// Copyright (C) 2000 - 2007. Charles W. Rapp.
// All Rights Reserved.
// 
// Contributor(s):
//
// statemap.java --
//
// This package defines the FSMContext class which must be
// inherited by any Java class wanting to use an smc-generated
// state machine.
//
// RCS ID
// $Id: FSMContext.java,v 1.2 2010/08/23 10:50:13 geolia Exp $
//
// CHANGE LOG
// (See the bottom of this file.)
//

package statemap;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.EmptyStackException;

//
// statemap.FSMContext --
//
// All Java classes wanting to use an SMC-generated state
// machine must extend this class. Since FSMContext provides
// functionality, it was not possible to implement it as an
// interface. See the SMC FAQ for how a class can use a state
// machine when that class is already extending another class.

public abstract class FSMContext implements Serializable {
	// ---------------------------------------------------------------
	// Member functions
	//

	// -----------------------------------------------------------
	// Constructors.
	//

	public FSMContext() {
		// There is no state until the application explicitly
		// sets the initial state.
		_name = "FSMContext";
		_state = null;
		_transition = "";
		_previousState = null;
		_stateStack = null;
		_debugFlag = false;
		_debugStream = System.err;
	} // end of FSMContext()

	//
	// end of Constructors.
	// -----------------------------------------------------------

	// -----------------------------------------------------------
	// Get methods.
	//

	// Returns the FSM name.
	public String getName() {
		return (_name);
	} // end of getName()

	// When debug is set to true, the state machine
	// will print messages to the console.
	public boolean getDebugFlag() {
		return (_debugFlag && _debugStream != null);
	} // end of getDebugFlag()

	// Write the debug output to this stream.
	public PrintStream getDebugStream() {
		return (_debugStream == null ? System.err : _debugStream);
	} // end of getDebugStream()

	// Is this state machine in a transition? If state is null,
	// then true; otherwise, false.
	public boolean isInTransition() {
		return (_state == null ? true : false);
	} // end of isInTransition()

	// NOTE: getState() is defined in the SMC-generated
	// FSMContext subclass.

	public State getPreviousState() throws NullPointerException {
		if (_previousState == null) {
			throw (new NullPointerException());
		} else {
			return (_previousState);
		}
	} // end of getPreviousState()

	public String getTransition() {
		return (_transition);
	} // end of getTransition()

	//
	// end of Get methods.
	// -----------------------------------------------------------

	// -----------------------------------------------------------
	// Set methods.
	//

	// Sets the FSM name.
	public void setName(String name) {
		if (name != null && name.length() > 0 && name.equals(_name) == false) {
			_name = name;
		}

		return;
	} // end of setName(String)

	public void setDebugFlag(boolean flag) {
		_debugFlag = flag;
		return;
	} // end of setDebugFlag(boolean)

	public void setDebugStream(PrintStream stream) {
		_debugStream = stream;
		return;
	} // end of setDebugStream(PrintStream)

	public void setState(State state) {
		State previousState = _state;

		if (getDebugFlag() == true) {
			getDebugStream().println("NEW STATE    : " + state.getName());
		}

		_state = state;

		return;
	} // end of setState(State)

	public void clearState() {
		_previousState = _state;
		_state = null;

		return;
	} // end of clearState()

	public void pushState(State state) {
		State previousState = _state;

		if (_state == null) {
			throw (new NullPointerException());
		}

		if (getDebugFlag() == true) {
			getDebugStream().println("PUSH TO STATE: " + state.getName());
		}

		if (_stateStack == null) {
			_stateStack = new java.util.Stack();
		}

		_stateStack.push(_state);
		_state = state;

		return;
	} // end of pushState(State)

	public void popState() throws EmptyStackException {
		if (_stateStack == null || _stateStack.isEmpty() == true) {
			if (getDebugFlag() == true) {
				getDebugStream().println("POPPING ON EMPTY STATE STACK.");
			}

			throw (new EmptyStackException());
		} else {
			State previousState = _state;

			// The pop method removes the top element
			// from the stack and returns it.
			_state = (State) _stateStack.pop();

			if (_stateStack.isEmpty() == true) {
				_stateStack = null;
			}

			if (getDebugFlag() == true) {
				getDebugStream().println("POP TO STATE : " + _state.getName());
			}
		}

		return;
	} // end of popState()

	public void emptyStateStack() {
		_stateStack.clear();
		_stateStack = null;

		return;
	} // end of emptyStateStack()

	//
	// end of Set methods.
	// -----------------------------------------------------------

	// ---------------------------------------------------------------
	// Member data
	//

	// The FSM name.
	transient protected String _name;

	// The current state.
	transient protected State _state;

	// The current transition *name*. Used for debugging
	// purposes.
	transient protected String _transition;

	// Remember what state a transition left.
	// Do no persist the previous state because an FSM should be
	// serialized while in transition.
	transient protected State _previousState;

	// This stack is used when a push transition is taken.
	transient protected java.util.Stack _stateStack;

	// When this flag is set to true, this class will print
	// out debug messages.
	transient protected boolean _debugFlag;

	// Write debug output to this stream.
	transient protected PrintStream _debugStream;

} // end of class FSMContext

//
// CHANGE LOG
// $Log: FSMContext.java,v $
// Revision 1.2  2010/08/23 10:50:13  geolia
// *** empty log message ***
//
// Revision 1.1  2010/08/18 12:35:57  geolia
// *** empty log message ***
//
// Revision 1.1  2010/08/18 10:54:29  geolia
// *** empty log message ***
//
// Revision 1.1  2010/08/13 12:10:57  geolia
// *** empty log message ***
//
// Revision 1.2  2008/06/18 13:04:19  cvs
// *** empty log message ***
//
// Revision 1.1 2008/03/21 11:29:49 cvs
// *** empty log message ***
//
// Revision 1.2 2008/02/22 12:59:27 cvs
// *** empty log message ***
//
// Revision 1.1 2008/02/21 14:28:20 cvs
// *** empty log message ***
//
// Revision 1.9 2007/08/05 13:00:34 cwrapp
// Version 5.0.1 check-in. See net/sf/smc/CODE_README.txt for more information.
//
// Revision 1.8 2007/02/21 13:50:59 cwrapp
// Moved Java code to release 1.5.0
//
// Revision 1.7 2005/05/28 18:44:13 cwrapp
// Updated C++, Java and Tcl libraries, added CSharp, Python
// and VB.
//
// Revision 1.1 2005/02/21 19:03:38 charlesr
// Variable name clean up.
//
// Revision 1.0 2003/12/14 20:38:40 charlesr
// Initial revision
//
