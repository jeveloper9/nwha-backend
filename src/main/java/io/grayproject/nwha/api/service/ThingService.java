package io.grayproject.nwha.api.service;

import io.grayproject.nwha.api.dto.RecentlyAddedThingDTO;
import io.grayproject.nwha.api.dto.ThingDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * @author Ilya Avkhimenya
 */
public interface ThingService {

    List<ThingDTO> getRandomThings(Integer limit);

    List<RecentlyAddedThingDTO> getRecentlyAddedThings();

    ThingDTO getThingById(Long id);

    ThingDTO createThing(Principal principal, ThingDTO thingDTO);

    ThingDTO updateThing(Principal principal, ThingDTO thingDTO);

    void deleteThing(Principal principal, Long id);

    ThingDTO setImageUrl(Principal principal, MultipartFile file, String thingId) throws IOException;
}
