package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.PigeonNotFoundException;
import com.richmax.dovenet.exception.UnauthorizedActionException;
import com.richmax.dovenet.exception.UserNotFoundException;
import com.richmax.dovenet.mapper.PigeonMapper;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.Pigeon;
import com.richmax.dovenet.repository.PigeonRepository;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.PigeonService;
import com.richmax.dovenet.service.data.PigeonDTO;
import com.richmax.dovenet.service.data.PigeonPedigreeDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;


import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Service
public class PigeonServiceImpl implements PigeonService {
    private final PigeonRepository pigeonRepository;
    private final PigeonMapper pigeonMapper;
    private final UserRepository userRepository;

    public PigeonServiceImpl(PigeonRepository pigeonRepository, PigeonMapper pigeonMapper, UserRepository userRepository) {
        this.pigeonRepository = pigeonRepository;
        this.pigeonMapper = pigeonMapper;
        this.userRepository = userRepository;
    }

    @Override
    public List<PigeonDTO> getAllPigeons(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username does not exist"));

        return pigeonRepository.findByOwner(owner).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public PigeonDTO getPigeonById(Long id, String username) {
        // Get user
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Username does not exist"));

        // Find pigeon
        Pigeon pigeon = pigeonRepository.findById(id)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon with ID " + id + " does not exist"));

        // Ensure it belongs to the authenticated user
        if (!pigeon.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You cannot access pigeons you don't own");
        }

        // Return DTO
        return convertToDto(pigeon);
    }

    @Override
    public PigeonDTO createPigeon(PigeonDTO pigeonDTO, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username does not exist"));

        Pigeon pigeon = convertToEntity(pigeonDTO);
        pigeon.setOwner(owner);
        Pigeon saved = pigeonRepository.save(pigeon);

        return convertToDto(saved);
    }

    @Override
    @Transactional
    public PigeonDTO updatePigeon(Long id, PigeonDTO pigeonDTO, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username does not exist"));

        Pigeon pigeon = pigeonRepository.findById(id)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon with ID " + id + " does not exist"));
        if (!pigeon.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedActionException("You cannot update pigeons you don’t own");
        }

        // Update allowed fields
        if (pigeonDTO.getName() != null) pigeon.setName(pigeonDTO.getName());
        if (pigeonDTO.getColor() != null) pigeon.setColor(pigeonDTO.getColor());
        if (pigeonDTO.getGender() != null) pigeon.setGender(pigeonDTO.getGender());
        if (pigeonDTO.getStatus() != null) pigeon.setStatus(pigeonDTO.getStatus());
        if (pigeonDTO.getBirthDate() != null) pigeon.setBirthDate(pigeonDTO.getBirthDate());
        if (pigeonDTO.getFatherRingNumber() != null) pigeon.setFatherRingNumber(pigeonDTO.getFatherRingNumber());
        if (pigeonDTO.getMotherRingNumber() != null) pigeon.setMotherRingNumber(pigeonDTO.getMotherRingNumber());

        // Allow transferring to a new owner
        if (pigeonDTO.getOwner() != null) {
            User newOwner = userRepository.findById(pigeonDTO.getOwner().getId())
                    .orElseThrow(() -> new RuntimeException("New owner does not exist"));
            pigeon.setOwner(newOwner);
        }

        return convertToDto(pigeonRepository.save(pigeon));
    }

    @Override
    public void deletePigeon(Long id, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username does not exist"));

        Pigeon pigeon = pigeonRepository.findById(id)
                .orElseThrow(() -> new PigeonNotFoundException("Pigeon does not exist"));

        if (!pigeon.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You cannot delete pigeons you don’t own");
        }

        pigeonRepository.deleteById(id);
    }

    public PigeonDTO convertToDto(Pigeon pigeon) {
        return pigeonMapper.toDto(pigeon);
    }

    public Pigeon convertToEntity(PigeonDTO pigeonDTO) {
        return pigeonMapper.toEntity(pigeonDTO);
    }

    @Override
    public PigeonPedigreeDTO getPedigree(Long pigeonId, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Username does not exist"));

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
    public byte[] generatePedigreePdf(Long id, String username) {
        PigeonPedigreeDTO pedigree = getPedigree(id, username);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfReader reader = new PdfReader("templates/DOVENET_pedigree.pdf");
            PdfStamper stamper = new PdfStamper(reader, baos);
            PdfContentByte canvas = stamper.getOverContent(1); // first page

            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
            canvas.setFontAndSize(bf, 12);
            canvas.setColorFill(Color.BLACK);

            // Example text overlay
            if(pedigree.getPigeon() != null) {
                fillPigeonBox(pedigree.getPigeon(), canvas, 20, 480);
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

            stamper.close();
            reader.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating pedigree PDF", e);
        }
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
        String textName = "Name: " + pigeon.getName();
        String textRing = "Ring: " + pigeon.getRingNumber();
        String textColor = "Color: " + pigeon.getColor();
        String textGender = "Gender: " + pigeon.getGender();

        addText(canvas, x, y, textRing,10, true);
        addText(canvas, x, y-20, textName,10,true);
        addText(canvas, x, y-40, textGender,10, false);
        addText(canvas, x, y-60, textColor,10,false);
    }

}
