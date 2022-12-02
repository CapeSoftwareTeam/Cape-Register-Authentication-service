package com.capeelectric.util;

/**
 * @author capeelectricsoftware
 *
 */
public class Constants {
	
	//RegistrationServiceImpl
	public static final String before_Approve_Permission = "NOT_AUTHORIZED";
	

	//SupplyCharacteristics
	public static final String supplyMainNominalCurrent = "0.00";

	public static final String supplyMainNominalFrequency = "0.00";

	public static final String supplyMainNominalVoltage = "0.00";

	public static final String supplyMainLoopImpedance = "0.000";

	public static final String supplyNominalFrequency = "0.00";

	public static final String supplyNominalVoltage = "0.00";

	public static final String supplyFaultCurrent = "0.00";

	public static final String supplyLoopImpedance = "0.000";
	
	public static final String supplyEarthResistance = "0.00";
	
	public static final String supplyGridResistance = "0.000";
	
	public static final String supplyEarthDepth = "0.0";
	
	public static final String supplyEarthElectrodeSize = "0.0";
	
	public static final String supplyEarthJointResistance = "0.000";
	
	public static final String supplyBoundingJointResistance = "0.000";
	
	public static final String supplyResidualTime = "0.00";
	
	public static final String supply_MainNominal_Current = "Supply_MainNominalCurrent";
	
	public static final String supply_MainNominal_Frequency = "Supply_MainNominalFrequency";
	
	public static final String supply_MainNominal_Voltage = "Supply_MainNominalVoltage";
	
	public static final String supply_MainLoop_Impedance = "Supply_MainLoopImpedance";
	
	public static final String supply_Nominal_Frequency = "Supply_NominalFrequency";
	
	public static final String supply_Nominal_Voltage = "Supply_NominalVoltage";
	
	public static final String supply_Fault_Current = "Supply_FaultCurrent";
	
	public static final String supply_LoopImpedance = "Supply_LoopImpedance";
	
	public static final String supply_Earth_Resistance = "supply_EarthResistance";
	
	public static final String supply_Grid_Resistance = "supply_GridResistance";
	
	public static final String supply_Earth_Depth = "supply_EarthDepth";
	
	public static final String supply_Earth_Electrode_Size = "supply_Earth_Electrode_Size";
	
	public static final String supply_Earth_JointResistance = "supply_EarthJointResistance";
	
	public static final String supply_Bounding_JointResistance = "supply_BoundingJointResistance";
	
	public static final String supply_ResidualTime = "supply_ResidualTime";
	

	//Testing
	public static final String testIncomingLoopimpedance = "0.000";

	public static final String testShortCircuit = "0.00";

	public static final String testInstantaneousDmt = "0.00";

	public static final String testInsulationResistance = "0.00";

	public static final String testVoltage = "0.00";

	public static final String loopImpedance = "0.000";

	public static final String faultCurrent = "0.00";
	
	public static final String testDisConnection = "0.000";
	
	public static final String conductorPhase = "0.0";
	
	public static final String conductorNeutral = "0.0";
	
	public static final String conductorPecpc = "0.0";
	
	public static final String continutiyApproximateLength = "0.0";
	
	public static final String continutiyRR = "0.000";
	
	public static final String continutiyR = "0.000";
	
	public static final String maxAllowedValue = "0.000";
	
	
	public static final String test_Incoming_LoopImpedance = "IncomingLoopImpedance";
	
	public static final String test_Short_CircuitSetting = "ShortCircuitSetting";
	
	public static final String test_EFSetting = "EFSetting";
	
	public static final String test_Insulation_Resistance = "InsulationResistance";
	
	public static final String test_Voltage = "Voltage";
	
	public static final String test_Loopimpedance = "Loopimpedance";
	
	public static final String test_Faultcurrent = "Faultcurrent";
	
	public static final String test_DisConnection = "Test_DisConnection";
	
	public static final String conductor_Phase = "conductor_Phase";
	
	public static final String conductor_Neutral = "conductor_Neutral";
	
	public static final String conductor_Pecpc = "conductor_Pecpc";
	
	public static final String continutiy_Approximate_Length = "continutiy_Approximate_Length";
	
	public static final String continutiy_RR = "continutiy_RR";
	
	public static final String continutiy_R = "continutiy_R";
	
	public static final String maxAllowed_Value = "maxAllowedValue";

	
	//Comments
	public static final String SEND_COMMENT = "SEND";
	
	public static final String REPLY_COMMENT = "REPLY";
	
	public static final String APPROVE_REJECT_COMMENT = "APPROVED";
	
	public static final String INTIAL_FLAG_VALUE = "0";
	
	public static final String INCREASED_FLAG_VALUE = "1";
	
	//EMAIL SUbject and URL
	public static final String EMAIL_SUBJECT_REGISTRATION = "Your request for accessing the SOLVE App is approved and you can generate OTP with this link";
	
	public static final String EMAIL_SUBJECT_URL_AWS = "https://www.safelv.net";
	
	public static final String EMAIL_SUBJECT_ADMIN_URL_AWS = "https://admin.safelv.net";
	
	public static final int LIST_ZERO = 0;
	
	public static final String EMAIL_SEND_COMMENT_MSG = "The Viewer has provided some comments or suggestion. You may need to login to SOLVE Application and check on the comments.";
	
	public static final String EMAIL_REPLY_COMMENT_MSG = "The Inspector has replied Your comments or suggestion. You may need to login to SOLVE Application and check on the comments.";
	
	public static final String EMAIL_APPROVE_COMMENT_MSG = "Your response to comments is satisfactory Approved.";
	
	public static final String EMAIL_REJECT_COMMENT_MSG = "Your response to comments was not satisfactory. Please provide valid response. \\n \\nYou may need to login to SOLVE Application and check on the comments.";

	public static final String NUMBER_OF_LICENSES = String.valueOf(5);

	public static final String FROM_EMAIL = "solve@safelv.in";
	
	public static final String AWS_EMAIL_PORT = String.valueOf(587);
	
	public static final String FIRST_PERSON_EMAIL = "sd@capeindia.net";
	
	public static final String SECOND_PERSON_EMAIL = "gk@capeindia.net";
	
	public static final String THIRD_PERSON_EMAIL = "deepak@capeindia.net";
	
	public static final String EMAIL_SUBJECT = "SOLVE Electrical Safety Digital TIC Platform";
	
	public static final String EMAIL_ADMIN_SUBJECT = "Approval For Inspector";
	
	public static final String EMAIL_HOST_NAME = "email-smtp.ap-south-1.amazonaws.com";
}
