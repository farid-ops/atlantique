package atlantique.cnut.ne.atlantique.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BlDto {
    private String designation;
    private String filename;
    private String mimetype;
    private String path;
}
