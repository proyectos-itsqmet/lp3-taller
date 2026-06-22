package com.lp3_taller.service;

import com.lp3_taller.model.Hotel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelService {

    private final List<Hotel> hoteles = List.of(
            new Hotel(1L, "Hotel Costa Guayaquil", "Guayaquil", 4, "04-2345678", "https://images.pexels.com/photos/27153434/pexels-photo-27153434.jpeg"),
            new Hotel(2L, "Hotel Los Andes", "Quito", 5, "02-2987654", "https://images.pexels.com/photos/29570237/pexels-photo-29570237.jpeg")
    );

    public List<Hotel> listar() {
        return hoteles;
    }

    public Optional<Hotel> buscarPorId(Long id) {
        return hoteles.stream()
                .filter(hotel -> hotel.getId().equals(id))
                .findFirst();
    }
}
