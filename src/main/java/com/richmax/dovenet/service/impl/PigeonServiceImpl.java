package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.LoftNotFoundException;
import com.richmax.dovenet.exception.PigeonNotFoundException;
import com.richmax.dovenet.exception.UnauthorizedActionException;
import com.richmax.dovenet.exception.UserNotFoundException;
import com.richmax.dovenet.mapper.CompetitionEntryMapper;
import com.richmax.dovenet.mapper.PigeonMapper;
import com.richmax.dovenet.repository.LoftRepository;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.Loft;
import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.repository.PigeonRepository;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.PigeonService;
import com.richmax.dovenet.service.data.CompetitionEntryDTO;
import com.richmax.dovenet.service.data.PigeonDTO;
import com.richmax.dovenet.service.data.PigeonPedigreeDTO;
import com.richmax.dovenet.types.SubscriptionType;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

@Service
public class PigeonServiceImpl implements PigeonService {
    private final PigeonRepository pigeonRepository;
    private final PigeonMapper pigeonMapper;
    private final CompetitionEntryMapper entryMapper;
    private final UserRepository userRepository;
    private final LoftRepository loftRepository;

    public PigeonServiceImpl(PigeonRepository pigeonRepository, PigeonMapper pigeonMapper, CompetitionEntryMapper entryMapper, UserRepository userRepository, LoftRepository loftRepository) {
        this.pigeonRepository = pigeonRepository;
        this.pigeonMapper = pigeonMapper;
        this.entryMapper = entryMapper;
        this.userRepository = userRepository;
        this.loftRepository = loftRepository;
    }

    @Override
    public List<PigeonDTO> getAllPigeons(String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " does not exist"));

        return pigeonRepository.findByOwner(owner).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public PigeonDTO getPigeonById(Long id, Authentication authentication) {
        // Find pigeon
        Pigeon pigeon = pigeonRepository.findById(id)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon with ID " + id + " does not exist"));

        // Check if user is admin
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // Ensure it belongs to the authenticated user OR user is admin
        if (!isAdmin && !pigeon.getOwner().getEmail().equals(authentication.getName())) {
            throw new UnauthorizedActionException("You cannot access pigeons you don't own");
        }

        // Return DTO
        return convertToDto(pigeon);
    }

    @Override
    public PigeonDTO getPublicPigeon(Long pigeonId) {
        // Find pigeon
        Pigeon pigeon = pigeonRepository.findById(pigeonId)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon with ID " + pigeonId + " does not exist"));
        PigeonDTO pigeonDTO = convertToDto(pigeon);
        pigeonDTO.setOwner(null);
        pigeonDTO.setLoftId(null);
        pigeonDTO.setLoft(null);
        Logger.getLogger("hi");
        return pigeonDTO;
    }

    @Override
    public PigeonDTO createPigeon(PigeonDTO pigeonDTO, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " does not exist"));

        Pigeon pigeon = convertToEntity(pigeonDTO);
        pigeon.setOwner(owner);

        if (pigeonDTO.getLoftId() != null) {
            Loft loft = loftRepository.findById(pigeonDTO.getLoftId())
                    .orElseThrow(() -> new LoftNotFoundException("Loft not found"));
            pigeon.setLoft(loft);
        }

        // Auto-create parents
        Pigeon father = autoCreateIfMissing(pigeon.getFatherRingNumber(), "male", owner);
        Pigeon mother = autoCreateIfMissing(pigeon.getMotherRingNumber(), "female",owner);

        Pigeon saved = pigeonRepository.save(pigeon);

        return convertToDto(saved);
    }

    @Override
    @Transactional
    public PigeonDTO updatePigeon(Long id, PigeonDTO pigeonDTO, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " does not exist"));

        Pigeon pigeon = pigeonRepository.findById(id)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon with ID " + id + " does not exist"));
        if (!pigeon.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You cannot update pigeons you don’t own");
        }

        // Update allowed fields
        if (pigeonDTO.getRingNumber() != null && !pigeonDTO.getRingNumber().equals(pigeon.getRingNumber())) {
            // Check uniqueness
            if (pigeonRepository.existsByRingNumber(pigeonDTO.getRingNumber())) {
                throw new RuntimeException("Ring number already in use");
            }
            pigeon.setRingNumber(pigeonDTO.getRingNumber());
        }

        if (pigeonDTO.getLoftId() != null) {
            Loft loft = loftRepository.findById(pigeonDTO.getLoftId())
                    .orElseThrow(() -> new LoftNotFoundException("Loft not found"));
            pigeon.setLoft(loft);
        }


        if (pigeonDTO.getName() != null) pigeon.setName(pigeonDTO.getName());
        if (pigeonDTO.getColor() != null) pigeon.setColor(pigeonDTO.getColor());
        if (pigeonDTO.getGender() != null) pigeon.setGender(pigeonDTO.getGender());
        if (pigeonDTO.getStatus() != null) pigeon.setStatus(pigeonDTO.getStatus());
        if (pigeonDTO.getBirthDate() != null) pigeon.setBirthDate(pigeonDTO.getBirthDate());

        if (pigeonDTO.getFatherRingNumber() != null) {
            autoCreateIfMissing(pigeonDTO.getFatherRingNumber(),"male", owner);
            pigeon.setFatherRingNumber(pigeonDTO.getFatherRingNumber());
        }
        if (pigeonDTO.getMotherRingNumber() != null) {
            autoCreateIfMissing(pigeonDTO.getMotherRingNumber(),"female", owner);
            pigeon.setMotherRingNumber(pigeonDTO.getMotherRingNumber());
        }

        // Allow transferring to a new owner
        if (pigeonDTO.getOwner() != null) {
            User newOwner = userRepository.findById(pigeonDTO.getOwner().getId())
                    .orElseThrow(() -> new RuntimeException("New owner does not exist"));
            pigeon.setOwner(newOwner);
        }

        return convertToDto(pigeonRepository.save(pigeon));
    }

    @Override
    public void deletePigeon(Long id, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " does not exist"));

        Pigeon pigeon = pigeonRepository.findById(id)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon does not exist"));

        if (!pigeon.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You cannot delete pigeons you don’t own");
        }

        pigeonRepository.deleteById(id);
    }

    public List<PigeonDTO> getPigeonParents(Long id, String email) {
        // Validate user exists
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " does not exist"));

        // Find pigeon by ID and ensure ownership
        Pigeon pigeon = pigeonRepository.findById(id)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon does not exist"));

        List<PigeonDTO> parents = new ArrayList<>();

        // --- Father ---
        if (pigeon.getFatherRingNumber() != null && !pigeon.getFatherRingNumber().isBlank()) {
            pigeonRepository.findByRingNumber(pigeon.getFatherRingNumber())
                    .ifPresentOrElse(
                            father -> parents.add(convertToDto(father)),
                            () -> {
                                throw new PigeonNotFoundException("Father with ring number " + pigeon.getFatherRingNumber() + " does not exist");
                            }
                    );
        }

        // --- Mother ---
        if (pigeon.getMotherRingNumber() != null && !pigeon.getMotherRingNumber().isBlank()) {
            pigeonRepository.findByRingNumber(pigeon.getMotherRingNumber())
                    .ifPresentOrElse(
                            mother -> parents.add(convertToDto(mother)),
                            () -> {
                                throw new PigeonNotFoundException("Mother with ring number " + pigeon.getMotherRingNumber() + " does not exist");
                            }
                    );
        }

        return parents;
    }

    @Override
    public PigeonPedigreeDTO getPedigree(Long pigeonId, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " does not exist"));

        Pigeon pigeon = pigeonRepository.findById(pigeonId)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon not found"));

        if (!pigeon.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You cannot view pedigrees of pigeons you don't own");
        }

        PigeonPedigreeDTO pedigree = new PigeonPedigreeDTO();
        pedigree.setPigeon(convertToDto(pigeon));

        // Parents
        Pigeon father = pigeonRepository.findByRingNumber(pigeon.getFatherRingNumber()).orElse(null);
        Pigeon mother = pigeonRepository.findByRingNumber(pigeon.getMotherRingNumber()).orElse(null);
        if (father != null) pedigree.setFather(convertToDto(father));
        if (mother != null) pedigree.setMother(convertToDto(mother));

        // Grandparents (if parents exist)
        if (father != null) {
            Pigeon paternalGrandfather = pigeonRepository.findByRingNumber(father.getFatherRingNumber()).orElse(null);
            Pigeon paternalGrandmother = pigeonRepository.findByRingNumber(father.getMotherRingNumber()).orElse(null);
            if (paternalGrandfather != null) pedigree.setPaternalGrandfather(convertToDto(paternalGrandfather));
            if (paternalGrandmother != null) pedigree.setPaternalGrandmother(convertToDto(paternalGrandmother));
        }
        if (mother != null) {
            Pigeon maternalGrandfather = pigeonRepository.findByRingNumber(mother.getFatherRingNumber()).orElse(null);
            Pigeon maternalGrandmother = pigeonRepository.findByRingNumber(mother.getMotherRingNumber()).orElse(null);
            if (maternalGrandfather != null) pedigree.setMaternalGrandfather(convertToDto(maternalGrandfather));
            if (maternalGrandmother != null) pedigree.setMaternalGrandmother(convertToDto(maternalGrandmother));
        }

        return pedigree;
    }

    @Override
    public byte[] generatePedigreePdf(Long id, String email) {
        PigeonPedigreeDTO pedigree = getPedigree(id, email);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfReader reader = new PdfReader("templates/Dovenet_pedigree_new.pdf");
            PdfStamper stamper = new PdfStamper(reader, baos);
            PdfContentByte canvas = stamper.getOverContent(1); // first page

            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
            canvas.setFontAndSize(bf, 12);
            canvas.setColorFill(Color.BLACK);

            // Example text overlay
            if(pedigree.getPigeon() != null) {
                fillPigeonBox(pedigree.getPigeon(), canvas, 20, 500);
            }

            if (pedigree.getFather() != null) {
                fillPigeonBox(pedigree.getFather(), canvas,160, 650);
            }

            if (pedigree.getMother() != null) {
                fillPigeonBox(pedigree.getMother(), canvas, 160, 290);
            }

            if(pedigree.getPaternalGrandfather() != null) {
                fillPigeonBox(pedigree.getPaternalGrandfather(), canvas, 305, 745);
            }

            if (pedigree.getPaternalGrandmother() != null) {
                fillPigeonBox(pedigree.getPaternalGrandmother(), canvas, 305, 560);
            }

            if (pedigree.getMaternalGrandfather() != null) {
                fillPigeonBox(pedigree.getMaternalGrandfather(), canvas, 305, 380);
            }

            if  (pedigree.getMaternalGrandmother() != null) {
                fillPigeonBox(pedigree.getMaternalGrandmother(), canvas, 305, 200);
            }

            PigeonPedigreeDTO fatherPedigree = getPedigree(pedigree.getFather().getId(), email);
            PigeonPedigreeDTO motherPedigree = getPedigree(pedigree.getMother().getId(), email);
            if(fatherPedigree != null) {
                if(fatherPedigree.getPaternalGrandfather() != null) {
                    fillPigeonBox(fatherPedigree.getPaternalGrandfather(), canvas, 450, 750);
                }

                if (fatherPedigree.getPaternalGrandmother() != null) {
                    fillPigeonBox(fatherPedigree.getPaternalGrandmother(), canvas, 450, 660);
                }

                if (fatherPedigree.getMaternalGrandfather() != null) {
                    fillPigeonBox(fatherPedigree.getMaternalGrandfather(), canvas, 450, 569);
                }

                if  (fatherPedigree.getMaternalGrandmother() != null) {
                    fillPigeonBox(fatherPedigree.getMaternalGrandmother(), canvas, 450, 478);
                }
            }


            if(motherPedigree != null) {
                if(motherPedigree.getPaternalGrandfather() != null) {
                    fillPigeonBox(motherPedigree.getPaternalGrandfather(), canvas, 450, 388);
                }

                if (motherPedigree.getPaternalGrandmother() != null) {
                    fillPigeonBox(motherPedigree.getPaternalGrandmother(), canvas, 450, 297);
                }

                if (motherPedigree.getMaternalGrandfather() != null) {
                    fillPigeonBox(motherPedigree.getMaternalGrandfather(), canvas, 450, 208);
                }

                if  (motherPedigree.getMaternalGrandmother() != null) {
                    fillPigeonBox(motherPedigree.getMaternalGrandmother(), canvas, 450, 118);
                }
            }

            // Add logo image
            Image logo = Image.getInstance(getClass().getResource("/templates/dovenet.jpeg"));

            // Scale the image (for example, 20% of original size)
            logo.scalePercent(10);

            logo.setAbsolutePosition(20, 750);

            canvas.addImage(logo);

            stamper.close();
            reader.close();
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace(); // log to console
            throw new RuntimeException("Error generating pedigree PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> searchRings(String q, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow();

        return pigeonRepository
                .findByOwnerAndRingNumberStartingWith(owner, q)
                .stream()
                .map(Pigeon::getRingNumber)
                .toList();
    }

    @Override
    public List<PigeonDTO> getPigeonChildren(Long id, String email) {
        // Validate user exists
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " does not exist"));

        // Find pigeon by ID
        Pigeon pigeon = pigeonRepository.findById(id)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon does not exist"));

        // Find all pigeons where this pigeon is the father or mother
        List<Pigeon> children = pigeonRepository.findByFatherRingNumberOrMotherRingNumber(
                pigeon.getRingNumber(), pigeon.getRingNumber()
        );

        List<Pigeon> ownedChildren = children.stream()
                .filter(c -> c.getOwner().getId().equals(owner.getId()))
                .toList();

        return ownedChildren.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<PigeonDTO> getPigeonsInLoft(Long loftId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Loft loft = loftRepository.findById(loftId)
                .orElseThrow(() -> new LoftNotFoundException("Loft not found"));

        if (!loft.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("You cannot view pigeons from another user's loft");
        }

        List<Pigeon> pigeons = pigeonRepository.findByLoftIdAndOwnerId(loftId, user.getId());

        return pigeons.stream().map(this::convertToDto).toList();
    }

    @Override
    public List<CompetitionEntryDTO> getCompetitionsForPigeon(Long pigeonId, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Pigeon pigeon = pigeonRepository.findById(pigeonId)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon not found"));

        if (!pigeon.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You cannot view pigeons you don't own");
        }

        return pigeon.getCompetitionEntries().stream()
                .map(entry -> entryMapper.toDto(entry))
                .toList();
    }

    //helpers
    public PigeonDTO convertToDto(Pigeon pigeon) {
        return pigeonMapper.toDto(pigeon);
    }

    public Pigeon convertToEntity(PigeonDTO pigeonDTO) {
        return pigeonMapper.toEntity(pigeonDTO);
    }

    private void addText(PdfContentByte canvas, float x, float y, String text, int fontSize, boolean bold) {
        if (text == null) return;
        try {
            BaseFont font = bold
                    ? BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED)
                    : BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);

            canvas.beginText();
            canvas.setFontAndSize(font, fontSize);
            canvas.showTextAligned(Element.ALIGN_LEFT, text, x, y, 0);
            canvas.endText();
        } catch (Exception e) {
            throw new RuntimeException("Error writing text to PDF", e);
        }
    }

    private void fillPigeonBox(PigeonDTO pigeon, PdfContentByte canvas, int x, int y) {

        int offset = 0;

        if (pigeon.getRingNumber() != null) {
            addText(canvas, x, y - offset, "Ring: " + pigeon.getRingNumber(), 8, true);
            offset += 20;
        }

        if (pigeon.getName() != null) {
            addText(canvas, x, y - offset, "Name: " + pigeon.getName(), 8, true);
            offset += 20;
        }

        if (pigeon.getGender() != null) {
            addText(canvas, x, y - offset, "Gender: " + pigeon.getGender(), 8, false);
            offset += 20;
        }

        if (pigeon.getColor() != null) {
            addText(canvas, x, y - offset, "Color: " + pigeon.getColor(), 8, false);
        }
    }

    private Pigeon autoCreateIfMissing(String ringNumber, String gender, User owner) {
        if (ringNumber == null || ringNumber.isBlank()) return null;

        return pigeonRepository.findByRingNumber(ringNumber)
                .orElseGet(() -> {
                    Pigeon p = new Pigeon();
                    p.setRingNumber(ringNumber);
                    p.setOwner(owner);
                    p.setStatus("unknown");
                    p.setGender(gender);
                    return pigeonRepository.save(p);
                });
    }

    public boolean hasActiveSubscription(User user) {
        return user.getSubscription() != SubscriptionType.FREE
                && user.getSubscriptionValidUntil() != null
                && user.getSubscriptionValidUntil().isAfter(LocalDateTime.now());
    }

}
