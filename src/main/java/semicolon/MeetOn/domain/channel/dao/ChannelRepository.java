package semicolon.MeetOn.domain.channel.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import semicolon.MeetOn.domain.channel.domain.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
}
