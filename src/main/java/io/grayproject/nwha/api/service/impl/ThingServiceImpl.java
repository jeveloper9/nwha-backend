package io.grayproject.nwha.api.service.impl;

import io.grayproject.nwha.api.domain.Profile;
import io.grayproject.nwha.api.domain.ProfileTask;
import io.grayproject.nwha.api.domain.Thing;
import io.grayproject.nwha.api.dto.ThingDTO;
import io.grayproject.nwha.api.exception.EntityNotFoundException;
import io.grayproject.nwha.api.mapper.ThingMapper;
import io.grayproject.nwha.api.repository.ProfileRepository;
import io.grayproject.nwha.api.repository.ProfileTaskRepository;
import io.grayproject.nwha.api.repository.ThingRepository;
import io.grayproject.nwha.api.service.ThingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Ilya Avkhimenya
 */
@Service
@RequiredArgsConstructor
public class ThingServiceImpl implements ThingService {
    private final ProfileRepository profileRepository;

    private final ThingMapper thingMapper;
    private final ThingRepository thingRepository;
    private final ProfileTaskRepository profileTaskRepository;

    // use only profileService
    public List<ThingDTO> getAllThingsByProfileId(Long profileId) {
        return profileRepository.findById(profileId)
                .map(Profile::getProfileTasks)
                .map(profileTasks -> profileTasks
                        .stream()
                        .map(ProfileTask::getThing)
                        .filter(Objects::nonNull)
                        .map(thingMapper)
                        .toList())
                // fatal error (this should not be)
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public List<ThingDTO> getRandomThings(Integer limit) {
        List<Thing> all = thingRepository.findAll();
        Collections.shuffle(all);
        return all.stream()
                .map(thingMapper)
                .limit(limit)
                .toList();
    }

    @Override
    public ThingDTO getThingById(Long id) {
        return thingRepository
                .findById(id)
                .map(thingMapper)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    @Override
    public ThingDTO createThing(Principal principal, ThingDTO thingDTO) {
        Profile profile = getProfileByPrincipal(principal)
                // fatal error (this should not be)
                .orElseThrow(RuntimeException::new);

        ProfileTask profileTask = profile
                .getProfileTasks()
                .stream()
                .filter(p -> p.getId().equals(thingDTO.profileTaskId()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Невозможно создать вещь для указанного profileTaskId"));

        Thing thing = Thing
                .builder()
                .removed(false)
                .archived(false)
                .description(thingDTO.description())
                .profileTask(profileTask)
                .fileUrl(thingDTO.fileUrl())
                .build();
        profileTask.setThing(thing);
        Thing saved = thingRepository.save(thing);
        profileTaskRepository.save(profileTask);
        return thingMapper.apply(saved);
    }

    @Override
    public ThingDTO updateThing(Principal principal, ThingDTO thingDTO) {
        return null;
    }

    @Override
    public void deleteThing(Principal principal, Long id) {

    }

    private Optional<Profile> getProfileByPrincipal(Principal principal) {
        return profileRepository
                .findProfileByUserUsername(principal.getName());
    }
}
