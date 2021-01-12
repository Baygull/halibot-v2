/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.drive.*;
import frc.power.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  Driver driver;
  PDP pdp;

  int autoStage;
  private static final String defaultAuto = "Default";
  private static final String auto1 = "Auto 1";
  private double[][] selectedAuto;
  private final SendableChooser<double[][]> chooser = new SendableChooser<>();
  

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    chooser.setDefaultOption("Default Auto", Autos.defaultAuto);
    //chooser.addOption("Auto 1", Autos.auto1);      
    // chooser.addOption("Aim Only", Autos.aimOnlyAuto);
    // chooser.addOption("Aim+Shoot", Autos.shootOnlyAuto);
    // chooser.addOption("Drive+Aim+Shoot Rightmost", Autos.runAimShootAutoRightmost);
    // chooser.addOption("Spinup Only", Autos.spinupOnlyAuto);
    // chooser.addOption("Drive+Aim+Shoot Rightmost goto Trench", Autos.runAimShootTrenchAutoRightmost);
    //chooser.addOption("Intake and Move", Autos.intakeOnlyAuto);
    // chooser.addOption("Intake, Move, Target", Autos.intakeTargetOnlyAuto);
    chooser.addOption("(Right) Intake 2 and Shoot", Autos.intakeSpinupTargetShootAuto);
    //chooser.addOption("Drive Forward Then Back", Autos.driveForwardThenReverse);
    chooser.addOption("(Right) Rendezvous + Trench", Autos.driveToRendezvousThenBackThenToTrench);
    chooser.addOption("(Left) Steal Two, Shoot", Autos.stealTwoAuto);
    chooser.addOption("(NEW) Turn North and Shoot", Autos.runForwardAimShootAuto);
    chooser.addOption("Do Nothing", Autos.nothingAuto);
    SmartDashboard.putData("Auto choices", chooser);

    driver = new Driver();
    driver.init();

    pdp = new PDP();
    pdp.init();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
    driver.setCurrentLimits(80);
    //driver.resetAuton();
    //driver.initPathfinderAuto();

    pdp.initLogger();
    selectedAuto = chooser.getSelected();
    setStuffUp();
    autoStage = 0;
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    SmartDashboard.putNumber("Auton Stage", autoStage);
    updateAuto(selectedAuto);
  }

  private boolean setupDone = false;
  private void setStuffUp(){
    if(!setupDone){
      driver.setupAuto();
      setupDone = true;
    }
  }

  @Override
  public void teleopInit() {
    driver.setCurrentLimits(50);
    //setStuffUp();
    //testInit();
    // shooter.initLogger();
    // pdp.initLogger();
    //driver.stopMotors();
    //shooter.initLogger();
    //pdp.initLogger();
    
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    driver.updateTeleop(); //USE FOR PRACTICE
    SmartDashboard.putNumber("drive omega", driver.omega());
  }

  @Override
  public void testInit() { 
    pdp.initLogger();
    driver.setupAuto();
    driver.initPoseLogger();
  }
  
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    //driver.updateTest(); //USE FOR POINT GATHERING
    //driver.updateTeleop(); //USE FOR PRACTICE
    SmartDashboard.putNumber("drive omega", driver.omega());
  }

  @Override
  public void disabledInit() {
    autoStage = 0;
    driver.setup = false;
    setupDone = false;
    driver.unlockWheels();
    pdp.closeLogger();
    driver.closeLogger();
    driver.setLowGear(false);
  }

  public void updateAuto(double[][] auto){
    driver.updateGeneric();
    if(auto[autoStage][3]==-1){
      if(driver.attackPoint(auto[autoStage][0], auto[autoStage][1], auto[autoStage][2])){autoStage++;}
    }
    else if(auto[autoStage][3]==-3){
      if(driver.attackPointReverse(auto[autoStage][0], auto[autoStage][1], auto[autoStage][2])){autoStage++;}
    }
    else if(auto[autoStage][3]==-2){
      //do nothing and don't advance the auton stage, as -2 signifies the end of the auton.
    }
    else{
      if(performSpecialAction(auto[autoStage][3])){autoStage++;}
    }
  }

  public boolean performSpecialAction(double actionToPerform){
    boolean complete = false;
    int action = (int)actionToPerform; //done because im lazy
    switch(action){
      case(0): 
        complete = specialAction0();
        break;
    }
    return complete;
  }

  private boolean specialAction0(){
    SmartDashboard.putString("Auto Mode", "Going Fishing");
    System.out.println("SPECIAL ACTION 0");
    return true;
  }

  // int cycles = 0;
  // private boolean specialActionAimTurret(double angle){
  //   cycles++;
  //   //turret.chasingTarget = true;
  //   SmartDashboard.putString("Auto Mode", "Aiming Turret to "+angle+" degrees for "+cycles+" cycles, chasingTarget = "+turret.chasingTarget);
  //   turret.setTargetAngle(angle);
  //   turret.chasingTarget = true;
  //   turret.track = true;
  //   turret.update();
  //   return turret.atTarget;
  // }

  // private boolean specialActionDisableTurret(){
  //   SmartDashboard.putString("Auto Mode", "Disabling Turret");
  //   turret.chasingTarget = false;
  //   turret.track = false;
  //   turret.update();
  //   return true;
  // }

  // private boolean specialActionSpinUpShooter(){
  //   baller.shooter.toggle(true);
  //   baller.updateMechanisms();
  //   return true;
  // }

  // private boolean specialActionSetupShooter(){
  //   baller.setupShooterTimer();
  //   cycles = 0;
  //   return true;
  // }
  // private boolean specialActionFireAll(){
  //   cycles++;
  //   baller.fireThreeBalls();
  //   SmartDashboard.putString("Auto Mode", "Shooting Balls for "+cycles+" cycles, "+baller.allBallsFired);
  //   //baller.update();
  //   return baller.allBallsFired;
  // }

  // private boolean specialActionEnableIntake(){
  //   baller.intake.setIntake(1);
  //   return true;
  // }

  // private boolean specialActionDisableIntake(){
  //   baller.intake.setIntake(0);
  //   return true;
  // }

  // private boolean specialActionDeployIntake(){
  //   baller.intake.setDeploy(true);
  //   return true;
  // }

  // private boolean specialActionRetractIntake(){
  //   baller.intake.setDeploy(false);
  //   return true;
  // }
}
