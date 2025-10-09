package com.alpha_code.alpha_code_activity_service.grpc.client;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import robot.Robot;
import robot.RobotServiceGrpc;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RobotServiceClient {

    @GrpcClient("alpha-robot-service")
    private RobotServiceGrpc.RobotServiceBlockingStub blockingStub;

    /**
     * Gọi gRPC lấy thông tin robot model theo id (single)
     */
    public Robot.RobotModelInformation getRobotModelInformation(String id) {
        log.info("gRPC → getRobotModelInformation(id={})", id);

        Robot.GetByIdRequest request = Robot.GetByIdRequest.newBuilder()
                .setId(id)
                .build();

        try {
            Robot.RobotModelInformation response = blockingStub.getRobotModel(request);
            log.info("gRPC ← getRobotModelInformation response: {}", response);
            return response;

        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed for id={}: {}", id, e.getStatus(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling RobotService for id={}", id, e);
            throw e;
        }
    }

    /**
     * Gọi gRPC lấy danh sách thông tin robot model theo list id
     */
    public Map<String, Robot.RobotModelInformation> getRobotModelsByIds(List<String> ids) {
        log.info("gRPC → getRobotModelsByIds, totalIds={}", ids.size());

        if (ids == null || ids.isEmpty()) {
            log.warn("getRobotModelsByIds called with empty ID list");
            return Collections.emptyMap();
        }

        Robot.GetByIdsRequest request = Robot.GetByIdsRequest.newBuilder()
                .addAllIds(ids)
                .build();

        try {
            Robot.RobotModelListResponse response = blockingStub.getRobotModelsByIds(request);
            List<Robot.RobotModelInformation> modelList = response.getModelsList();

            log.info("gRPC ← getRobotModelsByIds received {} models", modelList.size());

            // Map<id, modelInfo>
            return modelList.stream()
                    .collect(Collectors.toMap(Robot.RobotModelInformation::getId, Function.identity()));

        } catch (StatusRuntimeException e) {
            log.error("gRPC batch call failed: {}", e.getStatus(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in getRobotModelsByIds", e);
            throw e;
        }
    }
}
