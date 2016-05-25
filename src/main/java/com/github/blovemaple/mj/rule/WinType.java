package com.github.blovemaple.mj.rule;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.github.blovemaple.mj.object.PlayerInfo;
import com.github.blovemaple.mj.object.Tile;
import com.github.blovemaple.mj.object.TileType;
import com.github.blovemaple.mj.object.TileUnit;

/**
 * 和牌类型。
 * 
 * @author blovemaple <blovemaple2010(at)gmail.com>
 */
public interface WinType {
	/**
	 * 判断指定条件下是否可和牌。如果aliveTiles非null，则用于替换playerInfo中的信息做出判断，
	 * 否则利用playerInfo中的aliveTiles做出判断。
	 * 
	 * @param playerInfo
	 *            玩家信息
	 * @param aliveTiles
	 *            玩家手中的牌
	 * @return 是否可以和牌
	 */
	public default boolean match(PlayerInfo playerInfo, Set<Tile> aliveTiles) {
		Set<Tile> realAliveTiles = aliveTiles != null ? aliveTiles : playerInfo.getAliveTiles();
		return parseWinTileUnits(playerInfo, realAliveTiles).findAny().isPresent();
	}

	/**
	 * 全部解析成可以和牌的完整的TileUnit集合的流，失败返回空流。
	 * 
	 * @param playerInfo
	 *            除手牌之外的信息
	 * @param realAliveTiles
	 *            手牌
	 * @return 完整的TileUnit集合的流
	 */
	public Stream<Set<TileUnit>> parseWinTileUnits(PlayerInfo playerInfo, Set<Tile> readAliveTiles);

	/**
	 * 返回建议打出的牌，即从手牌中排除掉明显不应该打出的牌并返回。返回的列表按建议的优先级从高到低排列。
	 */
	public List<Tile> getDiscardCandidates(Set<Tile> aliveTiles, Collection<Tile> candidates);

	/**
	 * 获取ChangingForWin的流，移除changeCount个牌，增加(changeCount+1)个牌。
	 */
	public Stream<ChangingForWin> changingsForWin(PlayerInfo playerInfo, int changeCount, Collection<Tile> candidates);

	/**
	 * 一种结果时和牌的换牌方法，移除removedTiles并增加addedTiles。
	 * 
	 * @author blovemaple <blovemaple2010(at)gmail.com>
	 */
	public static class ChangingForWin {
		public Set<Tile> removedTiles, addedTiles;
		private int hashCode;

		public ChangingForWin(Set<Tile> removedTiles, Set<Tile> addedTiles) {
			this.removedTiles = removedTiles;
			this.addedTiles = addedTiles;
		}

		@Override
		public int hashCode() {
			if (hashCode == 0) {
				final int prime = 31;
				int result = 1;
				result = prime * result + addedTiles.stream().map(Tile::type).mapToInt(TileType::hashCode).sum();
				result = prime * result + removedTiles.stream().map(Tile::type).mapToInt(TileType::hashCode).sum();
				hashCode = result;
			}
			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof ChangingForWin))
				return false;
			ChangingForWin other = (ChangingForWin) obj;
			return hashCode() == other.hashCode();
		}

		@Override
		public String toString() {
			return "ChangingForWin [removedTiles=" + removedTiles + ", addedTiles=" + addedTiles + "]";
		}

	}
}
