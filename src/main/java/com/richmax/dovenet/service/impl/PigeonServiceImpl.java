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
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font labelFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font textFont = new Font(Font.HELVETICA, 12);
            Paragraph title = new Paragraph("Pigeon Pedigree", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Create a 3-column layout (grandparents - parents - pigeon)
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 1.5f, 2f});

            // --- Grandparents Row ---
            table.addCell(createPedigreeBox("Paternal Grandfather", pedigree.getPaternalGrandfather(), labelFont, textFont));
            table.addCell("");
            table.addCell(createPedigreeBox("Maternal Grandfather", pedigree.getMaternalGrandfather(), labelFont, textFont));

            table.addCell(createPedigreeBox("Paternal Grandmother", pedigree.getPaternalGrandmother(), labelFont, textFont));
            table.addCell("");
            table.addCell(createPedigreeBox("Maternal Grandmother", pedigree.getMaternalGrandmother(), labelFont, textFont));

            // --- Parents Row ---
            PdfPCell fatherCell = createPedigreeBox("Father", pedigree.getFather(), labelFont, textFont);
            fatherCell.setRowspan(1);
            PdfPCell motherCell = createPedigreeBox("Mother", pedigree.getMother(), labelFont, textFont);
            motherCell.setRowspan(1);

            table.addCell(fatherCell);
            table.addCell("");
            table.addCell(motherCell);

            // --- Main Pigeon Row ---
            PdfPCell mainPigeonCell = createPedigreeBox("Main Pigeon", pedigree.getPigeon(), labelFont, textFont);
            mainPigeonCell.setColspan(3);
            mainPigeonCell.setBackgroundColor(Color.LIGHT_GRAY);
            mainPigeonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(mainPigeonCell);

            document.add(table);

            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Generated on: " + LocalDate.now(), textFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating pedigree PDF", e);
        }
    }

    private PdfPCell createPedigreeBox(String title, PigeonDTO pigeon, Font labelFont, Font textFont) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(8);
        cell.setBorderColor(Color.DARK_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph titlePara = new Paragraph(title, labelFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(titlePara);

        if (pigeon != null) {
            cell.addElement(new Paragraph("Name: " + pigeon.getName(), textFont));
            cell.addElement(new Paragraph("Ring #: " + pigeon.getRingNumber(), textFont));
            if (pigeon.getColor() != null) cell.addElement(new Paragraph("Color: " + pigeon.getColor(), textFont));
            if (pigeon.getGender() != null) cell.addElement(new Paragraph("Gender: " + pigeon.getGender(), textFont));
            if (pigeon.getBirthDate() != null) cell.addElement(new Paragraph("Born: " + pigeon.getBirthDate(), textFont));
        } else {
            cell.addElement(new Paragraph("Unknown", textFont));
        }

        return cell;
    }


}
