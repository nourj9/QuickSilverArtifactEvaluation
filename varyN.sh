#!/bin/bash
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedStore
echo "Beginning test run for gen_DistributedStore";
  if [ $? -ne 124 ]; then
    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedStore 2 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedStore/gen_DistributedStore_c2.txt;
    if [ $? -ne 124 ]; then
      timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedStore 3 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedStore/gen_DistributedStore_c3.txt;
      if [ $? -ne 124 ]; then
        timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedStore 4 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedStore/gen_DistributedStore_c4.txt;
        if [ $? -ne 124 ]; then
          timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedStore 5 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedStore/gen_DistributedStore_c5.txt;
          if [ $? -ne 124 ]; then
            timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedStore 6 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedStore/gen_DistributedStore_c6.txt;
            if [ $? -ne 124 ]; then
              timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedStore 7 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedStore/gen_DistributedStore_c7.txt;
              if [ $? -ne 124 ]; then
                timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedStore 8 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedStore/gen_DistributedStore_c8.txt;
                if [ $? -ne 124 ]; then
                  timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedStore 9 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedStore/gen_DistributedStore_c9.txt;
                  if [ $? -ne 124 ]; then
                    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedStore 10 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedStore/gen_DistributedStore_c10.txt;
                  fi;
                fi;
              fi;
            fi;
          fi;
        fi;
      fi;
    fi;
  fi;
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_Consortium
echo "Beginning test run for gen_Consortium";
  if [ $? -ne 124 ]; then
    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_Consortium 2 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_Consortium/gen_Consortium_c2.txt;
    if [ $? -ne 124 ]; then
      timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_Consortium 3 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_Consortium/gen_Consortium_c3.txt;
      if [ $? -ne 124 ]; then
        timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_Consortium 4 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_Consortium/gen_Consortium_c4.txt;
        if [ $? -ne 124 ]; then
          timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_Consortium 5 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_Consortium/gen_Consortium_c5.txt;
          if [ $? -ne 124 ]; then
            timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_Consortium 6 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_Consortium/gen_Consortium_c6.txt;
            if [ $? -ne 124 ]; then
              timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_Consortium 7 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_Consortium/gen_Consortium_c7.txt;
              if [ $? -ne 124 ]; then
                timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_Consortium 8 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_Consortium/gen_Consortium_c8.txt;
                if [ $? -ne 124 ]; then
                  timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_Consortium 9 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_Consortium/gen_Consortium_c9.txt;
                  if [ $? -ne 124 ]; then
                    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_Consortium 10 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_Consortium/gen_Consortium_c10.txt;
                  fi;
                fi;
              fi;
            fi;
          fi;
        fi;
      fi;
    fi;
  fi;
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_TwoObjectTracker
echo "Beginning test run for gen_TwoObjectTracker";
  if [ $? -ne 124 ]; then
    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_TwoObjectTracker 2 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_TwoObjectTracker/gen_TwoObjectTracker_c2.txt;
    if [ $? -ne 124 ]; then
      timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_TwoObjectTracker 3 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_TwoObjectTracker/gen_TwoObjectTracker_c3.txt;
      if [ $? -ne 124 ]; then
        timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_TwoObjectTracker 4 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_TwoObjectTracker/gen_TwoObjectTracker_c4.txt;
        if [ $? -ne 124 ]; then
          timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_TwoObjectTracker 5 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_TwoObjectTracker/gen_TwoObjectTracker_c5.txt;
          if [ $? -ne 124 ]; then
            timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_TwoObjectTracker 6 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_TwoObjectTracker/gen_TwoObjectTracker_c6.txt;
            if [ $? -ne 124 ]; then
              timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_TwoObjectTracker 7 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_TwoObjectTracker/gen_TwoObjectTracker_c7.txt;
              if [ $? -ne 124 ]; then
                timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_TwoObjectTracker 8 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_TwoObjectTracker/gen_TwoObjectTracker_c8.txt;
                if [ $? -ne 124 ]; then
                  timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_TwoObjectTracker 9 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_TwoObjectTracker/gen_TwoObjectTracker_c9.txt;
                  if [ $? -ne 124 ]; then
                    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_TwoObjectTracker 10 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_TwoObjectTracker/gen_TwoObjectTracker_c10.txt;
                  fi;
                fi;
              fi;
            fi;
          fi;
        fi;
      fi;
    fi;
  fi;
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRobotFlocking
echo "Beginning test run for gen_DistributedRobotFlocking";
  if [ $? -ne 124 ]; then
    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRobotFlocking 2 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRobotFlocking/gen_DistributedRobotFlocking_c2.txt;
    if [ $? -ne 124 ]; then
      timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRobotFlocking 3 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRobotFlocking/gen_DistributedRobotFlocking_c3.txt;
      if [ $? -ne 124 ]; then
        timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRobotFlocking 4 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRobotFlocking/gen_DistributedRobotFlocking_c4.txt;
        if [ $? -ne 124 ]; then
          timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRobotFlocking 5 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRobotFlocking/gen_DistributedRobotFlocking_c5.txt;
          if [ $? -ne 124 ]; then
            timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRobotFlocking 6 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRobotFlocking/gen_DistributedRobotFlocking_c6.txt;
            if [ $? -ne 124 ]; then
              timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRobotFlocking 7 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRobotFlocking/gen_DistributedRobotFlocking_c7.txt;
              if [ $? -ne 124 ]; then
                timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRobotFlocking 8 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRobotFlocking/gen_DistributedRobotFlocking_c8.txt;
                if [ $? -ne 124 ]; then
                  timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRobotFlocking 9 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRobotFlocking/gen_DistributedRobotFlocking_c9.txt;
                  if [ $? -ne 124 ]; then
                    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRobotFlocking 10 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRobotFlocking/gen_DistributedRobotFlocking_c10.txt;
                  fi;
                fi;
              fi;
            fi;
          fi;
        fi;
      fi;
    fi;
  fi;
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedLockService
echo "Beginning test run for gen_DistributedLockService";
  if [ $? -ne 124 ]; then
    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedLockService 2 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedLockService/gen_DistributedLockService_c2.txt;
    if [ $? -ne 124 ]; then
      timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedLockService 3 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedLockService/gen_DistributedLockService_c3.txt;
      if [ $? -ne 124 ]; then
        timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedLockService 4 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedLockService/gen_DistributedLockService_c4.txt;
        if [ $? -ne 124 ]; then
          timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedLockService 5 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedLockService/gen_DistributedLockService_c5.txt;
          if [ $? -ne 124 ]; then
            timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedLockService 6 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedLockService/gen_DistributedLockService_c6.txt;
            if [ $? -ne 124 ]; then
              timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedLockService 7 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedLockService/gen_DistributedLockService_c7.txt;
              if [ $? -ne 124 ]; then
                timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedLockService 8 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedLockService/gen_DistributedLockService_c8.txt;
                if [ $? -ne 124 ]; then
                  timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedLockService 9 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedLockService/gen_DistributedLockService_c9.txt;
                  if [ $? -ne 124 ]; then
                    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedLockService 10 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedLockService/gen_DistributedLockService_c10.txt;
                  fi;
                fi;
              fi;
            fi;
          fi;
        fi;
      fi;
    fi;
  fi;
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedSensorNetwork
echo "Beginning test run for gen_DistributedSensorNetwork";
  if [ $? -ne 124 ]; then
    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedSensorNetwork 2 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedSensorNetwork/gen_DistributedSensorNetwork_c2.txt;
    if [ $? -ne 124 ]; then
      timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedSensorNetwork 3 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedSensorNetwork/gen_DistributedSensorNetwork_c3.txt;
      if [ $? -ne 124 ]; then
        timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedSensorNetwork 4 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedSensorNetwork/gen_DistributedSensorNetwork_c4.txt;
        if [ $? -ne 124 ]; then
          timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedSensorNetwork 5 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedSensorNetwork/gen_DistributedSensorNetwork_c5.txt;
          if [ $? -ne 124 ]; then
            timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedSensorNetwork 6 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedSensorNetwork/gen_DistributedSensorNetwork_c6.txt;
            if [ $? -ne 124 ]; then
              timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedSensorNetwork 7 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedSensorNetwork/gen_DistributedSensorNetwork_c7.txt;
              if [ $? -ne 124 ]; then
                timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedSensorNetwork 8 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedSensorNetwork/gen_DistributedSensorNetwork_c8.txt;
                if [ $? -ne 124 ]; then
                  timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedSensorNetwork 9 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedSensorNetwork/gen_DistributedSensorNetwork_c9.txt;
                  if [ $? -ne 124 ]; then
                    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedSensorNetwork 10 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedSensorNetwork/gen_DistributedSensorNetwork_c10.txt;
                  fi;
                fi;
              fi;
            fi;
          fi;
        fi;
      fi;
    fi;
  fi;
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SensorNetworkwithReset
echo "Beginning test run for gen_SensorNetworkwithReset";
  if [ $? -ne 124 ]; then
    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SensorNetworkwithReset 2 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SensorNetworkwithReset/gen_SensorNetworkwithReset_c2.txt;
    if [ $? -ne 124 ]; then
      timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SensorNetworkwithReset 3 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SensorNetworkwithReset/gen_SensorNetworkwithReset_c3.txt;
      if [ $? -ne 124 ]; then
        timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SensorNetworkwithReset 4 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SensorNetworkwithReset/gen_SensorNetworkwithReset_c4.txt;
        if [ $? -ne 124 ]; then
          timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SensorNetworkwithReset 5 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SensorNetworkwithReset/gen_SensorNetworkwithReset_c5.txt;
          if [ $? -ne 124 ]; then
            timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SensorNetworkwithReset 6 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SensorNetworkwithReset/gen_SensorNetworkwithReset_c6.txt;
            if [ $? -ne 124 ]; then
              timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SensorNetworkwithReset 7 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SensorNetworkwithReset/gen_SensorNetworkwithReset_c7.txt;
              if [ $? -ne 124 ]; then
                timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SensorNetworkwithReset 8 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SensorNetworkwithReset/gen_SensorNetworkwithReset_c8.txt;
                if [ $? -ne 124 ]; then
                  timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SensorNetworkwithReset 9 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SensorNetworkwithReset/gen_SensorNetworkwithReset_c9.txt;
                  if [ $? -ne 124 ]; then
                    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SensorNetworkwithReset 10 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SensorNetworkwithReset/gen_SensorNetworkwithReset_c10.txt;
                  fi;
                fi;
              fi;
            fi;
          fi;
        fi;
      fi;
    fi;
  fi;
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol
echo "Beginning test run for gen_SATSLandingProtocol";
  if [ $? -ne 124 ]; then
    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol 2 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol/gen_SATSLandingProtocol_c2.txt;
    if [ $? -ne 124 ]; then
      timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol 3 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol/gen_SATSLandingProtocol_c3.txt;
      if [ $? -ne 124 ]; then
        timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol 4 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol/gen_SATSLandingProtocol_c4.txt;
        if [ $? -ne 124 ]; then
          timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol 5 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol/gen_SATSLandingProtocol_c5.txt;
          if [ $? -ne 124 ]; then
            timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol 6 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol/gen_SATSLandingProtocol_c6.txt;
            if [ $? -ne 124 ]; then
              timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol 7 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol/gen_SATSLandingProtocol_c7.txt;
              if [ $? -ne 124 ]; then
                timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol 8 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol/gen_SATSLandingProtocol_c8.txt;
                if [ $? -ne 124 ]; then
                  timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol 9 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol/gen_SATSLandingProtocol_c9.txt;
                  if [ $? -ne 124 ]; then
                    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol 10 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol/gen_SATSLandingProtocol_c10.txt;
                  fi;
                fi;
              fi;
            fi;
          fi;
        fi;
      fi;
    fi;
  fi;
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol_II
echo "Beginning test run for gen_SATSLandingProtocol_II";
  if [ $? -ne 124 ]; then
    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol_II 2 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol_II/gen_SATSLandingProtocol_II_c2.txt;
    if [ $? -ne 124 ]; then
      timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol_II 3 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol_II/gen_SATSLandingProtocol_II_c3.txt;
      if [ $? -ne 124 ]; then
        timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol_II 4 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol_II/gen_SATSLandingProtocol_II_c4.txt;
        if [ $? -ne 124 ]; then
          timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol_II 5 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol_II/gen_SATSLandingProtocol_II_c5.txt;
          if [ $? -ne 124 ]; then
            timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol_II 6 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol_II/gen_SATSLandingProtocol_II_c6.txt;
            if [ $? -ne 124 ]; then
              timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol_II 7 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol_II/gen_SATSLandingProtocol_II_c7.txt;
              if [ $? -ne 124 ]; then
                timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol_II 8 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol_II/gen_SATSLandingProtocol_II_c8.txt;
                if [ $? -ne 124 ]; then
                  timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol_II 9 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol_II/gen_SATSLandingProtocol_II_c9.txt;
                  if [ $? -ne 124 ]; then
                    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_SATSLandingProtocol_II 10 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_SATSLandingProtocol_II/gen_SATSLandingProtocol_II_c10.txt;
                  fi;
                fi;
              fi;
            fi;
          fi;
        fi;
      fi;
    fi;
  fi;
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticsMotionPlanning
echo "Beginning test run for gen_MobileRoboticsMotionPlanning";
  if [ $? -ne 124 ]; then
    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticsMotionPlanning 2 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticsMotionPlanning/gen_MobileRoboticsMotionPlanning_c2.txt;
    if [ $? -ne 124 ]; then
      timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticsMotionPlanning 3 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticsMotionPlanning/gen_MobileRoboticsMotionPlanning_c3.txt;
      if [ $? -ne 124 ]; then
        timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticsMotionPlanning 4 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticsMotionPlanning/gen_MobileRoboticsMotionPlanning_c4.txt;
        if [ $? -ne 124 ]; then
          timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticsMotionPlanning 5 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticsMotionPlanning/gen_MobileRoboticsMotionPlanning_c5.txt;
          if [ $? -ne 124 ]; then
            timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticsMotionPlanning 6 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticsMotionPlanning/gen_MobileRoboticsMotionPlanning_c6.txt;
            if [ $? -ne 124 ]; then
              timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticsMotionPlanning 7 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticsMotionPlanning/gen_MobileRoboticsMotionPlanning_c7.txt;
              if [ $? -ne 124 ]; then
                timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticsMotionPlanning 8 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticsMotionPlanning/gen_MobileRoboticsMotionPlanning_c8.txt;
                if [ $? -ne 124 ]; then
                  timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticsMotionPlanning 9 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticsMotionPlanning/gen_MobileRoboticsMotionPlanning_c9.txt;
                  if [ $? -ne 124 ]; then
                    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticsMotionPlanning 10 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticsMotionPlanning/gen_MobileRoboticsMotionPlanning_c10.txt;
                  fi;
                fi;
              fi;
            fi;
          fi;
        fi;
      fi;
    fi;
  fi;
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticswithReset
echo "Beginning test run for gen_MobileRoboticswithReset";
  if [ $? -ne 124 ]; then
    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticswithReset 2 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticswithReset/gen_MobileRoboticswithReset_c2.txt;
    if [ $? -ne 124 ]; then
      timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticswithReset 3 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticswithReset/gen_MobileRoboticswithReset_c3.txt;
      if [ $? -ne 124 ]; then
        timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticswithReset 4 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticswithReset/gen_MobileRoboticswithReset_c4.txt;
        if [ $? -ne 124 ]; then
          timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticswithReset 5 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticswithReset/gen_MobileRoboticswithReset_c5.txt;
          if [ $? -ne 124 ]; then
            timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticswithReset 6 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticswithReset/gen_MobileRoboticswithReset_c6.txt;
            if [ $? -ne 124 ]; then
              timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticswithReset 7 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticswithReset/gen_MobileRoboticswithReset_c7.txt;
              if [ $? -ne 124 ]; then
                timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticswithReset 8 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticswithReset/gen_MobileRoboticswithReset_c8.txt;
                if [ $? -ne 124 ]; then
                  timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticswithReset 9 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticswithReset/gen_MobileRoboticswithReset_c9.txt;
                  if [ $? -ne 124 ]; then
                    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_MobileRoboticswithReset 10 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_MobileRoboticswithReset/gen_MobileRoboticswithReset_c10.txt;
                  fi;
                fi;
              fi;
            fi;
          fi;
        fi;
      fi;
    fi;
  fi;
mkdir -p /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRegister
echo "Beginning test run for gen_DistributedRegister";
  if [ $? -ne 124 ]; then
    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRegister 2 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRegister/gen_DistributedRegister_c2.txt;
    if [ $? -ne 124 ]; then
      timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRegister 3 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRegister/gen_DistributedRegister_c3.txt;
      if [ $? -ne 124 ]; then
        timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRegister 4 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRegister/gen_DistributedRegister_c4.txt;
        if [ $? -ne 124 ]; then
          timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRegister 5 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRegister/gen_DistributedRegister_c5.txt;
          if [ $? -ne 124 ]; then
            timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRegister 6 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRegister/gen_DistributedRegister_c6.txt;
            if [ $? -ne 124 ]; then
              timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRegister 7 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRegister/gen_DistributedRegister_c7.txt;
              if [ $? -ne 124 ]; then
                timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRegister 8 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRegister/gen_DistributedRegister_c8.txt;
                if [ $? -ne 124 ]; then
                  timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRegister 9 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRegister/gen_DistributedRegister_c9.txt;
                  if [ $? -ne 124 ]; then
                    timeout 1800s /home/ae/kinara/test/mc/benchmarks/bin/opt/gen_DistributedRegister 10 > /home/ae/kinara/test/mc/benchmarks/oopsla21_benchmark_results/VaryingN/gen_DistributedRegister/gen_DistributedRegister_c10.txt;
                  fi;
                fi;
              fi;
            fi;
          fi;
        fi;
      fi;
    fi;
  fi;
