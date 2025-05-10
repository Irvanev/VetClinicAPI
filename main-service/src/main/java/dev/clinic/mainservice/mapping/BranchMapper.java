package dev.clinic.mainservice.mapping;

import dev.clinic.mainservice.dtos.appointments.AppointmentResponseOwner;
import dev.clinic.mainservice.dtos.branches.BranchRequest;
import dev.clinic.mainservice.dtos.branches.BranchResponse;
import dev.clinic.mainservice.models.entities.Appointment;
import dev.clinic.mainservice.models.entities.Branches;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class BranchMapper {

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public static Branches toRequest(BranchRequest branchRequest) {
        if (branchRequest == null) {
            return null;
        }
        Branches branch = new Branches();
        branch.setAddress(branchRequest.getAddress());
        branch.setPhone(branchRequest.getPhone());
        branch.setEmail(branchRequest.getEmail());
        branch.setName(branchRequest.getName());
        branch.setShortName(branchRequest.getShortName());
        Point point = createPoint(
                branchRequest.getLongitude(),
                branchRequest.getLatitude()
        );
        branch.setCoordinates(point);

        if (branchRequest.getServices() != null) {
            branch.setServices(new HashSet<>(branchRequest.getServices()));
        }

        return branch;
    }

    public static BranchResponse toResponse(Branches branches) {
        if (branches == null) {
            return null;
        }
        BranchResponse branchResponse = new BranchResponse();
        branchResponse.setId(branches.getId());
        branchResponse.setAddress(branches.getAddress());
        branchResponse.setPhone(branches.getPhone());
        branchResponse.setEmail(branches.getEmail());
        branchResponse.setLatitude(branches.getCoordinates().getY());
        branchResponse.setLongitude(branches.getCoordinates().getX());
        branchResponse.setName(branches.getName());
        branchResponse.setShortName(branches.getShortName());

        if (branches.getServices() != null) {
            branchResponse.setServices(new HashSet<>(branches.getServices()));
        }

        return branchResponse;
    }

    public static List<BranchResponse> toResponseList(List<Branches> branches) {
        if (branches == null) {
            return Collections.emptyList();
        }
        return branches.stream()
                .map(BranchMapper::toResponse)
                .collect(Collectors.toList());
    }

    private static Point createPoint(Double longitude, Double latitude) {
        if (longitude == null || latitude == null) {
            return null;
        }
        Coordinate coordinate = new Coordinate(longitude, latitude);
        return geometryFactory.createPoint(coordinate);
    }
}
