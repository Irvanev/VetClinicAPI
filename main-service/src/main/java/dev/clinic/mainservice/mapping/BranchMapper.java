package dev.clinic.mainservice.mapping;

import dev.clinic.mainservice.dtos.branches.BranchRequest;
import dev.clinic.mainservice.dtos.branches.BranchResponse;
import dev.clinic.mainservice.models.entities.Branches;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.HashSet;

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

    public static void updateFromRequest(Branches branch, BranchRequest request) {
        if (request.getAddress() != null) {
            branch.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            branch.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            branch.setEmail(request.getEmail());
        }
        if (request.getName() != null) {
            branch.setName(request.getName());
        }
        if (request.getShortName() != null) {
            branch.setShortName(request.getShortName());
        }
        Point point = createPoint(
                request.getLongitude(),
                request.getLatitude()
        );
        branch.setCoordinates(point);

        if (request.getServices() != null) {
            branch.setServices(new HashSet<>(request.getServices()));
        }
    }

    private static Point createPoint(Double longitude, Double latitude) {
        if (longitude == null || latitude == null) {
            return null;
        }
        Coordinate coordinate = new Coordinate(longitude, latitude);
        return geometryFactory.createPoint(coordinate);
    }
}
